package com.example.tablenow.domain.holidays;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.store.StoreRepository;

@ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
@DataJpaTest // DB 관련 컴포넌트만 메모리에 로딩 (예. Repository)
public class HolidaysRepositoryTest {
    
    @Autowired
    private HolidaysRepository holidaysRepository;

    @Autowired
    private StoreRepository storeRepository;

    Store storePS; // 정기휴무 등록한 Store 데이터
    String holidays = "00 10 20 30 40 50"; // 매주 월요일 휴무

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        holidaysRepository.save(Holidays.builder().holidays(holidays).store(storePS).build());
    }

    // 정기휴무 등록
    @Test
    public void save() {
        // given
        String holidays = "01 11 21 31 41 51"; // 매주 화요일 휴무

        // when
        Holidays holidaysPS = holidaysRepository.save(Holidays.builder().holidays(holidays).store(storePS).build());

        // then
        assertThat(holidaysPS.getHolidays()).isEqualTo(holidays);
    }

    // Holidays 데이터 영속화
    @Test
    public void findByStoreId() {
        // given

        // when
        Holidays holidaysPS = holidaysRepository.findByStoreId(storePS.getId());

        // then
        assertThat(holidaysPS.getHolidays()).isEqualTo(holidays);
    }

    // 정기휴무 조회
    @Test
    public void mFindHolidaysByStoreId() {
        // given

        // when
        String holidaysResult = holidaysRepository.mFindHolidaysByStoreId(storePS.getId());

        // then
        assertThat(holidaysResult).isEqualTo(holidays);
    }
}
