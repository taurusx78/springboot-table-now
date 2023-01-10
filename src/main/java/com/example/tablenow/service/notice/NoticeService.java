package com.example.tablenow.service.notice;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tablenow.domain.holidays.HolidaysRepository;
import com.example.tablenow.domain.hours.Hours;
import com.example.tablenow.domain.hours.HoursRepository;
import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.notice.NoticeRepository;
import com.example.tablenow.domain.notice_image.NoticeImage;
import com.example.tablenow.domain.notice_image.NoticeImageRepository;
import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.store.StoreRepository;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.domain.today_hours.TodayHoursRepository;
import com.example.tablenow.handler.exception.CustomException;
import com.example.tablenow.service.store.StoreCommonService;
import com.example.tablenow.web.dto.notice.NoticeReqDto.SaveNoticeReqDto;
import com.example.tablenow.web.dto.notice.NoticeRespDto;
import com.example.tablenow.web.dto.notice.NoticeRespDto.NoticeListRespDto;
import com.example.tablenow.web.dto.notice.NoticeRespDto.SaveNoticeRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.TodayHoursRespDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final StoreRepository storeRepository;
    private final TodayHoursRepository todayHoursRepository;
    private final HolidaysRepository holidaysRepository;
    private final HoursRepository hoursRepository;

    private final StoreCommonService commonService;

    // application.yml에 설정된 파일 업로드 경로 가져옴
    @Value("${file.path}")
    private String imagesFolder; // C:/sts-4.14.0.RELEASE/workspace/images/

    @Transactional
    public SaveNoticeRespDto 알림등록(Long storeId, Long userId, SaveNoticeReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        int addedCount = dto.getAddedImageFileList() == null ? 0 : dto.getAddedImageFileList().size(); // 추가된 사진 개수

        // 최대 개수 초과 여부 확인
        if (addedCount > 2) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("알림 첨부사진을 2장 이하로 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // (Tip!) Store는 id만 필요하기 때문에, 전체 데이터를 SELECT 할 필요 없음
        Store store = Store.builder().id(storeId).build();

        // 1. Notice 테이블에 INSERT
        Notice noticeEntity = noticeRepository.save(dto.toNoticeEntity(store));

        // 2. 임시휴무 유무
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);
        if (!dto.getHolidayStartDate().equals("")) {
            // 오늘 영업일이면서 임시휴무에 포함되는 경우, TodayHours 테이블 UPDATE
            if (dto.getHolidayStartDate().compareTo(LocalDate.now().toString()) <= 0
                    && todayHoursEntity.getHolidayType() == 1) {
                todayHoursEntity.update(3, "없음", "없음", "없음");
            }
        }

        List<NoticeImage> noticeImageList = new ArrayList<>(); // 테이블 INSERT용
        // 3. 첨부사진 유무
        if (addedCount > 0) {
            List<String> noticeImageUrlList = new ArrayList<>(); // 서버 저장용
            for (MultipartFile file : dto.getAddedImageFileList()) {
                String imageUrl = commonService.makeImageUrl(file);
                noticeImageList.add(dto.toNoticeImageEntity(imageUrl, noticeEntity));
                noticeImageUrlList.add(imageUrl);
            }
            // NoticeImage 테이블에 INSERT
            noticeImageRepository.saveAll(noticeImageList);
            // 서버에 첨부사진 저장
            commonService.saveImagesToServer("notice", noticeImageUrlList, dto.getAddedImageFileList());
        }

        // 4. 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHoursEntity.getHolidayType(),
                todayHoursEntity.getBusinessHours(), todayHoursEntity.getBreakTime(), todayHoursEntity.getLastOrder());

        return new SaveNoticeRespDto(noticeEntity, noticeImageList, todayHoursEntity, state);
    }

    @Transactional(readOnly = true)
    public NoticeListRespDto 알림전체조회(Long storeId) {
        List<Notice> noticeEntityList = noticeRepository.findAllByStoreId(storeId);
        NoticeListRespDto dtoList = new NoticeListRespDto(noticeEntityList.stream()
                .map(notice -> new NoticeRespDto(notice, notice.getNoticeImageList())).toList());
        return dtoList;
    }

    @Transactional
    public SaveNoticeRespDto 알림수정(Long storeId, Long userId, Long noticeId, SaveNoticeReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        int imageCount = noticeImageRepository.mCountAllByStoreId(storeId); // 기존 사진 개수
        int deletedCount = dto.getDeletedImageUrlList() == null ? 0 : dto.getDeletedImageUrlList().size(); // 삭제된 사진 개수
        int addedCount = dto.getAddedImageFileList() == null ? 0 : dto.getAddedImageFileList().size(); // 추가된 사진 개수

        // 최대 개수 초과 여부 확인 (기존 - 삭제 + 추가 > 최대)
        if (imageCount - deletedCount + addedCount > 2) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("알림 첨부사진을 2장 이하로 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // 1. 알림 데이터 영속화
        Notice noticeEntity = noticeRepository.findById(noticeId).orElseThrow(() -> {
            throw new NoResultException("존재하지 않는 알림");
        });

        // 2. 알림 데이터 변경
        noticeEntity.update(dto.getTitle(), dto.getContent(), dto.getHolidayStartDate(), dto.getHolidayEndDate());

        // 3. 임시휴무가 변경된 경우, TodayHours 테이블 UPDATE
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);
        LocalDate today = LocalDate.now();
        if (todayHoursEntity.getHolidayType() == 1 && !dto.getHolidayStartDate().equals("")
                && dto.getHolidayStartDate().compareTo(today.toString()) <= 0) {
            // 1. 오늘 영업일 -> 임시휴무 변경
            todayHoursEntity.update(3, "없음", "없음", "없음");
        } else if (todayHoursEntity.getHolidayType() == 3 && (dto.getHolidayStartDate().equals("")
                || dto.getHolidayStartDate().compareTo(today.toString()) > 0)) {
            // 2. 오늘 임시휴무 -> 영업일 변경 (임시휴무를 삭제하거나 시작일이 연기된 경우)
            Hours hoursEntity = hoursRepository.findByDayTypeAndStoreId(today.getDayOfWeek().getValue() - 1,
                    storeId);
            todayHoursEntity.update(1, hoursEntity.getBusinessHours(), hoursEntity.getBreakTime(),
                    hoursEntity.getLastOrder());
        }

        // 4. 추가된 첨부사진이 있는 경우
        List<NoticeImage> noticeImageList = new ArrayList<>();
        List<String> noticeImageUrlList = new ArrayList<>();
        if (addedCount > 0) {
            for (MultipartFile file : dto.getAddedImageFileList()) {
                String imageUrl = commonService.makeImageUrl(file);
                noticeImageList.add(dto.toNoticeImageEntity(imageUrl, noticeEntity));
                noticeImageUrlList.add(imageUrl);
            }
            // NoticeImage 테이블에 INSERT
            noticeImageRepository.saveAll(noticeImageList);
        }

        // 5. 삭제된 첨부사진이 있는 경우
        if (deletedCount > 0) {
            for (String imageUrl : dto.getDeletedImageUrlList()) {
                // NoticeImage 테이블에서 DELETE
                noticeImageRepository.deleteByImageUrl(imageUrl);
            }
        }

        // 추가된 첨부사진 서버에 저장
        if (addedCount > 0) {
            commonService.saveImagesToServer("notice", noticeImageUrlList, dto.getAddedImageFileList());
        }
        // 삭제된 첨부사진 서버에서 삭제
        if (deletedCount > 0) {
            commonService.deleteImagesOnServer("notice", dto.getDeletedImageUrlList());
        }

        // 6. 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHoursEntity.getHolidayType(),
                todayHoursEntity.getBusinessHours(), todayHoursEntity.getBreakTime(), todayHoursEntity.getLastOrder());

        return new SaveNoticeRespDto(noticeEntity, noticeImageList, todayHoursEntity, state);
    }

    @Transactional
    public TodayHoursRespDto 알림삭제(Long storeId, Long userId, Long noticeId) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        // 알림 데이터 영속화
        Notice noticeEntity = noticeRepository.findById(noticeId).orElseThrow(() -> {
            throw new NoResultException("존재하지 않는 알림");
        });

        // 오늘 임시휴무였던 경우, TodayHours 테이블 UPDATE
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);
        LocalDate today = LocalDate.now();
        if (!noticeEntity.getHolidayStartDate().equals("")
                && noticeEntity.getHolidayStartDate().compareTo(today.toString()) <= 0) {
            String holidays = holidaysRepository.mFindHolidaysByStoreId(storeId);
            int weekOfMonth = today.get(WeekFields.of(Locale.getDefault()).weekOfMonth()); // 오늘 주차
            int dayOfWeek = today.getDayOfWeek().getValue() - 1; // 오늘 요일

            if (holidays.contains(String.valueOf(weekOfMonth * 10 + dayOfWeek))) {
                // 정기휴무인 경우
                todayHoursEntity.update(2, "없음", "없음", "없음");
            } else {
                // 영업일인 경우
                Hours hoursEntity = hoursRepository.findByDayTypeAndStoreId(today.getDayOfWeek().getValue() - 1,
                        storeId);
                todayHoursEntity.update(1, hoursEntity.getBusinessHours(), hoursEntity.getBreakTime(),
                        hoursEntity.getLastOrder());
            }
        }

        // 1. 알림 첨부사진 Url 전체 SELECT
        List<String> noticeImageUrlList = noticeImageRepository.mFindAllImageUrlByNoticeId(noticeId);

        // 2. 알림 첨부사진 전체 삭제
        noticeImageRepository.deleteAllByNoticeId(noticeId);

        // 3. 알림 삭제
        noticeRepository.deleteById(noticeId);

        // 4. 서버에서 첨부사진 삭제
        commonService.deleteImagesOnServer("notice", noticeImageUrlList);

        // 5. 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHoursEntity.getHolidayType(), todayHoursEntity.getBusinessHours(),
                todayHoursEntity.getBreakTime(), todayHoursEntity.getLastOrder());
        return new TodayHoursRespDto(todayHoursEntity, state);
    }
}
