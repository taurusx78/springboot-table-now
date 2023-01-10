package com.example.tablenow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.tablenow.domain.holidays.HolidaysRepository;
import com.example.tablenow.domain.hours.Hours;
import com.example.tablenow.domain.hours.HoursRepository;
import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.notice.NoticeRepository;
import com.example.tablenow.domain.notice_image.NoticeImage;
import com.example.tablenow.domain.notice_image.NoticeImageRepository;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.domain.today_hours.TodayHoursRepository;
import com.example.tablenow.service.store.StoreCommonService;

// Repository를 제외한 Service만 테스트하기 위해 가짜데이터 생성
// @Mock: 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito 환경에 주입

@ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
@ExtendWith(MockitoExtension.class) // 가짜 메모리 환경 생성
public class StoreCommonServiceTest {

    @InjectMocks // 가짜데이터 주입 받음
    private StoreCommonService commonService;

    @Mock // 가짜 객체 생성
    private HoursRepository hoursRepository;

    @Mock
    private HolidaysRepository holidaysRepository;

    @Mock
    private TodayHoursRepository todayHoursRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private NoticeImageRepository noticeImageRepository;

    @Test
    public void 오늘의영업시간수정_날짜변경() {
        // given
        LocalDate today = LocalDate.now();
        Long storeId = 1L;
        Long noticeId = 1L;

        // stub
        // 가설 1. todayHoursRepository의 findByStoreId() 실행 시 TodayHours 엔티티 리턴 (어제까지 알림등록
        // 임시휴무)
        LocalDateTime createdDate = LocalDateTime.now();
        TodayHours todayHours = TodayHours.builder().holidayType(3).businessHours("없음")
                .breakTime("없음").lastOrder("없음").today(today.minusDays(1)).modifiedDate(createdDate)
                .build();
        when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

        // 가설 2. holidaysRepository의 mFindHolidaysByStoreId() 실행 시 빈 공백 리턴 (오늘 정기휴무 아님)
        when(holidaysRepository.mFindHolidaysByStoreId(storeId)).thenReturn("");

        // 가설 3. noticeRepository의 findAllByStoreId() 실행 시 List<Notice> 타입 리턴 (임시휴무 지남)
        NoticeImage noticeImage = NoticeImage.builder().imageUrl("noticeImageUrl.png").build();
        Notice notice = Notice.builder().id(noticeId).title("알림 제목").title("알림 내용")
                .holidayStartDate(today.minusDays(1).toString()).holidayEndDate(today.minusDays(1).toString())
                .noticeImageList(List.of(noticeImage)).createdDate(createdDate).build();
        when(noticeRepository.findAllByStoreId(storeId)).thenReturn(List.of(notice));

        // 가설 4. noticeImageRepository의 mFindAllImageUrlByNoticeId() 실행 시 List<String> 타입 리턴
        when(noticeImageRepository.mFindAllImageUrlByNoticeId(noticeId)).thenReturn(List.of());

        // 가설 5. hoursRepository의 findByDayTypeAndStoreId() 실행 시 Hours 엔티티 리턴 (오늘 영업일)
        Hours hours = Hours.builder().businessHours("10:00~22:00").breakTime("15:00~17:00").lastOrder("21:30").build();
        when(hoursRepository.findByDayTypeAndStoreId(today.getDayOfWeek().getValue() - 1, storeId)).thenReturn(hours);

        // when
        TodayHours todayHoursEntity = commonService.오늘의영업시간수정_날짜변경(today, storeId);

        // then
        assertThat(todayHoursEntity.getHolidayType()).isEqualTo(1);
        assertThat(todayHoursEntity.getBusinessHours()).isEqualTo(hours.getBusinessHours());
        assertThat(todayHoursEntity.getBreakTime()).isEqualTo(hours.getBreakTime());
        assertThat(todayHoursEntity.getLastOrder()).isEqualTo(hours.getLastOrder());
    }
}
