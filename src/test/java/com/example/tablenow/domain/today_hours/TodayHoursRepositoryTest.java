package com.example.tablenow.domain.today_hours;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.store.StoreRepository;

@ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
@DataJpaTest // DB 관련 컴포넌트만 메모리에 로딩 (예. Repository)
public class TodayHoursRepositoryTest {

    @Autowired
    private TodayHoursRepository todayHoursRepository;

    @Autowired
    private StoreRepository storeRepository;

    Store storePS; // 영업시간 등록한 Store 데이터
    int holidayType = 1; // 영업일
    String businessHours = "10:00~22:00";
    String breakTime = "15:00~17:00";
    String lastOrder = "21:30";
    LocalDate today = LocalDate.now();

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        // 1. Store 등록
        storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        // 2. TodayHours 등록
        todayHoursRepository.save(TodayHours.builder().holidayType(holidayType).businessHours(businessHours)
                .breakTime(breakTime).lastOrder(lastOrder).today(today).store(storePS).build());
    }

    // 오늘의 영업시간 등록
    @Test
    public void save() {
        // given
        int holidayType = 2; // 정기휴무
        String businessHours = "없음";
        String breakTime = "없음";
        String lastOrder = "없음";
        LocalDate today = LocalDate.now();
        TodayHours todayHours = TodayHours.builder().holidayType(holidayType).businessHours(businessHours)
                .breakTime(breakTime).lastOrder(lastOrder).today(today).store(storePS).build();

        // when
        TodayHours todayHoursPS = todayHoursRepository.save(todayHours);

        // then
        assertThat(todayHoursPS.getHolidayType()).isEqualTo(holidayType);
        assertThat(todayHoursPS.getBusinessHours()).isEqualTo(businessHours);
        assertThat(todayHoursPS.getBreakTime()).isEqualTo(breakTime);
        assertThat(todayHoursPS.getLastOrder()).isEqualTo(lastOrder);
        assertThat(todayHoursPS.getToday()).isEqualTo(today);
    }

    // TodayHours 데이터 영속화
    @Test
    public void findByStoreId() {
        // given

        // when
        TodayHours todayHoursPS = todayHoursRepository.findByStoreId(storePS.getId());

        // then
        assertThat(todayHoursPS.getHolidayType()).isEqualTo(holidayType);
        assertThat(todayHoursPS.getBusinessHours()).isEqualTo(businessHours);
        assertThat(todayHoursPS.getBreakTime()).isEqualTo(breakTime);
        assertThat(todayHoursPS.getLastOrder()).isEqualTo(lastOrder);
        assertThat(todayHoursPS.getToday()).isEqualTo(today);
    }
    
    // 등록된 날짜 조회
    @Test
    public void mFindTodayByStoreId() {
        // given

        // when
        Date dbToday = todayHoursRepository.mFindTodayByStoreId(storePS.getId());

        // then
        assertThat(dbToday.toLocalDate()).isEqualTo(today);
    }
}
