package com.example.tablenow.service.store;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tablenow.domain.holidays.HolidaysRepository;
import com.example.tablenow.domain.hours.Hours;
import com.example.tablenow.domain.hours.HoursRepository;
import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.notice.NoticeRepository;
import com.example.tablenow.domain.notice_image.NoticeImageRepository;
import com.example.tablenow.domain.tables.Tables;
import com.example.tablenow.domain.tables.TablesRepository;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.domain.today_hours.TodayHoursRepository;

import lombok.RequiredArgsConstructor;

// 매장용, 고객용 공통 서비스 함수

@RequiredArgsConstructor
@Service
public class StoreCommonService {

    private final HolidaysRepository holidaysRepository;
    private final HoursRepository hoursRepository;
    private final TodayHoursRepository todayHoursRepository;
    private final TablesRepository tablesRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;

    // application.yml에 설정된 파일 업로드 경로 가져옴
    @Value("${file.path}")
    private String imagesFolder; // C:/sts-4.14.0.RELEASE/workspace/images/

    // 더티체킹은 영속화된 데이터의 변경이 없으면 UPDATE 실행하지 않음 (수정일 그대로 유지됨)
    @Transactional
    public TodayHours 오늘의영업시간수정_날짜변경(LocalDate today, Long storeId) {
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);
        todayHoursEntity.changeToday(today);

        // 오늘의 주차
        int weekOfMonth = today.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
        // 오늘의 요일: 월(0)~일(6)
        int dayOfWeek = today.getDayOfWeek().getValue() - 1;

        // 1. 정기휴무 여부
        String holidays = holidaysRepository.mFindHolidaysByStoreId(storeId);
        if (holidays.contains(String.valueOf(weekOfMonth * 10 + dayOfWeek))) {
            todayHoursEntity.update(2, "없음", "없음", "없음");
            return todayHoursEntity;
        }

        // 2. 임시휴무 여부
        List<Notice> noticeList = noticeRepository.findAllByStoreId(storeId);
        String todayStr = today.toString();
        for (Notice notice : noticeList) {
            // 임시휴무가 있을 때,
            if (!notice.getHolidayStartDate().equals("")) {
                if (notice.getHolidayStartDate().compareTo(todayStr) <= 0
                        && notice.getHolidayEndDate().compareTo(todayStr) >= 0) {
                    // 2-1. 오늘이 임시휴무 기간에 포함되는 경우
                    todayHoursEntity.update(3, "없음", "없음", "없음");
                    return todayHoursEntity;
                } else if (notice.getHolidayEndDate().compareTo(todayStr) < 0) {
                    // 2-2. 임시휴무가 끝난 경우, 알림 자동 삭제
                    알림삭제(storeId, notice.getId());
                }
            }
        }

        // 3. 영업시간 반영
        Hours hoursEntity = hoursRepository.findByDayTypeAndStoreId(dayOfWeek, storeId);
        todayHoursEntity.update(1, hoursEntity.getBusinessHours(), hoursEntity.getBreakTime(),
                hoursEntity.getLastOrder());
        return todayHoursEntity;
    }

    @Transactional
    public void 알림삭제(Long storeId, Long noticeId) {
        // 1. 알림 첨부사진 Url 전체 SELECT
        List<String> noticeImageUrlList = noticeImageRepository.mFindAllImageUrlByNoticeId(noticeId);

        // 2. 알림 첨부사진 전체 삭제
        noticeImageRepository.deleteAllByNoticeId(noticeId);

        // 3. 알림 삭제
        noticeRepository.deleteById(noticeId);

        // 4. 서버에서 첨부사진 삭제
        deleteImagesOnServer("notice", noticeImageUrlList);
    }

    // 영업상태 설정
    @Transactional
    public String setState(Long storeId, String now, int holidayType, String businessHours, String breakTime,
            String lastOrder) {
        String state = "";
        if (holidayType == 1) {
            // 1. 영업시간 여부
            if (checkTimeInRange(now, businessHours)) {
                // 2. 휴게시간 여부
                if (!breakTime.equals("없음") && checkTimeInRange(now, breakTime)) {
                    state = "휴게시간";
                } else if (!lastOrder.equals("없음") && now.compareTo(lastOrder) > 0) {
                    // 3. 주문마감 여부
                    state = "주문마감";
                } else {
                    state = "영업중";
                }
            } else {
                state = "준비중";
            }
        } else if (holidayType == 2) {
            state = "정기휴무";
        } else {
            state = "임시휴무";
        }
        // 영업중이 아닌 경우 잔여테이블 수 초기화
        if (!state.equals("영업중")) {
            // Tables 데이터 영속화
            Tables tableEntity = tablesRepository.findByStoreId(storeId);
            tableEntity.updateTableCountOnly(tableEntity.getAllTableCount());
        }
        return state;
    }

    // 현재시간(now)이 주어진 시간범위(range) 내에 있는지 확인
    public boolean checkTimeInRange(String nowTime, String range) {
        String[] time = range.split(" - ");
        boolean isInRange = false;

        if (time[0].compareTo(time[1]) < 0) {
            // 시작시간이 끝시간보다 작은 경우 (Ex. 10:00 - 22:00)
            if (nowTime.compareTo(time[0]) >= 0 && nowTime.compareTo(time[1]) <= 0) {
                isInRange = true;
            }
        } else {
            // 시작시간이 끝시간보다 큰 경우 (Ex. 16:00 - 09:00)
            if (!(nowTime.compareTo(time[1]) >= 0 && nowTime.compareTo(time[0]) <= 0)) {
                isInRange = true;
            }
        }
        return isInRange;
    }

    // UUID 이용해 파일 이름 생성 (중복방지)
    public String makeImageUrl(MultipartFile file) {
        String fileName = file.getOriginalFilename(); // 파일명
        if (fileName != null) {
            String extension = fileName.substring(fileName.lastIndexOf("."), fileName.length()); // 파일 확장자
            UUID uuid = UUID.randomUUID();
            return uuid + extension;
        }
        return null;
    }

    // 서버에 이미지 저장
    public void saveImagesToServer(String type, List<String> imageUrlList, List<MultipartFile> fileList) {
        for (int i = 0, len = imageUrlList.size(); i < len; i++) {
            // 저장 경로 설정
            Path filePath = Paths.get(imagesFolder + type + "/" + imageUrlList.get(i));
            // 서버에 이미지 저장
            try {
                Files.write(filePath, fileList.get(i).getBytes());
            } catch (Exception e) {
                new RuntimeException("이미지 저장중 오류 발생");
            }
        }
    }

    // 서버에서 이미지 삭제
    public void deleteImagesOnServer(String type, List<String> imageUrlList) {
        for (String imageUrl : imageUrlList) {
            File file = new File(imagesFolder + type + "/" + imageUrl);
            if (file.exists()) { // 파일이 존재하면
                file.delete(); // 파일 삭제
            }
        }
    }
}
