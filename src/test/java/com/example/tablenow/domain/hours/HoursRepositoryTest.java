package com.example.tablenow.domain.hours;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.store.StoreRepository;

@ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
@DataJpaTest // DB 관련 컴포넌트만 메모리에 로딩 (예. Repository)
public class HoursRepositoryTest {

    @Autowired
    private HoursRepository hoursRepository;

    @Autowired
    private StoreRepository storeRepository;

    Store storePS; // 영업시간 등록한 Store 데이터
    List<String> businessHoursList = Arrays.asList("10:00~22:00", "10:00~22:00", "10:00~22:00", "10:00~22:00",
            "10:00~22:00", "10:00~22:00", "10:00~22:00");
    List<String> breakTimeList = Arrays.asList("15:00~17:00", "15:00~17:00", "15:00~17:00", "15:00~17:00",
            "15:00~17:00", "15:00~17:00", "15:00~17:00");
    List<String> lastOrderList = Arrays.asList("21:30", "21:30", "21:30", "21:30", "21:30", "21:30", "21:30");

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        // 1. Store 등록
        storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        // 2. Hours 전체등록
        List<Hours> hoursList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            hoursList.add(Hours.builder().dayType(i).businessHours(businessHoursList.get(i))
                    .breakTime(breakTimeList.get(i)).lastOrder(lastOrderList.get(i)).store(storePS).build());
        }
        hoursRepository.saveAll(hoursList);
    }

    // 영업시간 전체등록
    @Test
    public void saveAll() {
        // given
        Store storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        List<Hours> hoursList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            hoursList.add(Hours.builder().dayType(i).businessHours(businessHoursList.get(i))
                    .breakTime(breakTimeList.get(i)).lastOrder(lastOrderList.get(i)).store(storePS).build());
        }

        // when
        List<Hours> hoursPSList = hoursRepository.saveAll(hoursList);

        // then
        for (int i = 0; i < 7; i++) {
            assertThat(hoursPSList.get(i).getBusinessHours()).isEqualTo(businessHoursList.get(i));
            assertThat(hoursPSList.get(i).getBreakTime()).isEqualTo(breakTimeList.get(i));
            assertThat(hoursPSList.get(i).getLastOrder()).isEqualTo(lastOrderList.get(i));
        }
    }

    // 영업시간 전체조회
    @Test
    public void findAllByStoreId() {
        // given

        // when
        List<Hours> hoursPSList = hoursRepository.findAllByStoreId(storePS.getId());

        // then
        for (int i = 0; i < 7; i++) {
            assertThat(hoursPSList.get(i).getBusinessHours()).isEqualTo(businessHoursList.get(i));
            assertThat(hoursPSList.get(i).getBreakTime()).isEqualTo(breakTimeList.get(i));
            assertThat(hoursPSList.get(i).getLastOrder()).isEqualTo(lastOrderList.get(i));
        }
    }

    // 영업시간 1개 조회
    @Test
    public void findByDayTypeAndStoreId() {
        // given
        int dayType = 0;

        // when
        Hours hoursPS = hoursRepository.findByDayTypeAndStoreId(dayType, storePS.getId());

        // then
        assertThat(hoursPS.getBusinessHours()).isEqualTo(businessHoursList.get(dayType));
        assertThat(hoursPS.getBreakTime()).isEqualTo(breakTimeList.get(dayType));
        assertThat(hoursPS.getLastOrder()).isEqualTo(lastOrderList.get(dayType));
    }
}
