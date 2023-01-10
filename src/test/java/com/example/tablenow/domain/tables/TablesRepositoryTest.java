package com.example.tablenow.domain.tables;

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
public class TablesRepositoryTest {

    @Autowired
    private TablesRepository tablesRepository;

    @Autowired
    private StoreRepository storeRepository;

    Store storePS; // 영업시간 등록한 Store 데이터
    int allTableCount = 10; // 전체테이블 수

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        // 1. Store 등록
        storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        // 2. Tables 등록
        tablesRepository
                .save(Tables.builder().allTableCount(allTableCount).tableCount(allTableCount).paused(false)
                        .store(storePS).build());
    }

    // Tables 등록
    @Test
    public void save() {
        // given
        int allTableCount = 15; // 전체테이블 수
        Tables tables = Tables.builder().allTableCount(allTableCount).tableCount(allTableCount).paused(false)
                .store(storePS).build();

        // when
        Tables tablesPS = tablesRepository.save(tables);

        // then
        assertThat(tablesPS.getAllTableCount()).isEqualTo(allTableCount);
        assertThat(tablesPS.getTableCount()).isEqualTo(allTableCount);
        assertThat(tablesPS.isPaused()).isEqualTo(false);
    }

    // 전체테이블 수 조회
    @Test
    public void mFindAllTableCountByStoreId() {
        // given

        // when
        int allTableCountResult = tablesRepository.mFindAllTableCountByStoreId(storePS.getId());

        // then
        assertThat(allTableCountResult).isEqualTo(allTableCount);
    }

    // Tables 데이터 영속화
    @Test
    public void findByStoreId() {
        // given

        // when
        Tables tablesPS = tablesRepository.findByStoreId(storePS.getId());

        // then
        assertThat(tablesPS.getAllTableCount()).isEqualTo(allTableCount);
    }
}
