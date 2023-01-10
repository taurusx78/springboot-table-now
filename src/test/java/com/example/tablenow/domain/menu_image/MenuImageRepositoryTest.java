package com.example.tablenow.domain.menu_image;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
public class MenuImageRepositoryTest {
    
    @Autowired
    private MenuImageRepository menuImageRepository;

    @Autowired
    private StoreRepository storeRepository;

    Store storePS; // 메뉴사진 등록한 Store 데이터

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        // 1. Store 등록
        storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        // 2. MenuImage 전체등록
        List<MenuImage> menuImageList = new ArrayList<>();
        MenuImage menuImage = MenuImage.builder().imageUrl("imageUrl1.png").store(storePS).build();
        menuImageList.add(menuImage);
        menuImageRepository.saveAll(menuImageList);
    }

    // 메뉴사진 전체등록
    @Test
    public void saveAll() {
        // given
        List<MenuImage> menuImageList = new ArrayList<>();
        MenuImage menuImage = MenuImage.builder().imageUrl("imageUrl2.png").store(storePS).build();
        menuImageList.add(menuImage);

        // when
        List<MenuImage> menuImagePSList = menuImageRepository.saveAll(menuImageList);

        // then
        assertThat(menuImagePSList.size()).isEqualTo(menuImageList.size());
        assertThat(menuImagePSList.get(0).getImageUrl()).isEqualTo(menuImageList.get(0).getImageUrl());
    }

    // 메뉴사진 전체조회
    @Test
    public void findAllByStoreId() {
        // given
        String imageUrl = "imageUrl1.png";

        // when
        List<MenuImage> menuImageList = menuImageRepository.findAllByStoreId(storePS.getId());

        // then
        assertThat(menuImageList.size()).isEqualTo(1);
        assertThat(menuImageList.get(0).getImageUrl()).isEqualTo(imageUrl);
    }

    // 메뉴사진 URL 리스트
    @Test
    public void mFindAllImageUrlByStoreId() {
        // given
        String imageUrl = "imageUrl1.png";

        // when
        List<String> imageUrlList = menuImageRepository.mFindAllImageUrlByStoreId(storePS.getId());

        // then
        assertThat(imageUrlList.size()).isEqualTo(1);
        assertThat(imageUrlList.get(0)).isEqualTo(imageUrl);
    }
}
