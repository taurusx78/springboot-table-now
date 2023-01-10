package com.example.tablenow.domain.basic_image;

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
public class BasicImageRepositoryTest {

    @Autowired
    private BasicImageRepository basicImageRepository;

    @Autowired
    private StoreRepository storeRepository;

    Store storePS; // 대표사진 등록한 Store 데이터

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        // 1. Store 등록
        storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        // 2. BasicImage 전체등록
        List<BasicImage> basicImageList = new ArrayList<>();
        BasicImage basicImage = BasicImage.builder().imageUrl("imageUrl1.png").store(storePS).build();
        basicImageList.add(basicImage);
        basicImageRepository.saveAll(basicImageList);
    }

    // 대표사진 전체등록
    @Test
    public void saveAll() {
        // given
        List<BasicImage> basicImageList = new ArrayList<>();
        BasicImage basicImage = BasicImage.builder().imageUrl("imageUrl2.png").store(storePS).build();
        basicImageList.add(basicImage);

        // when
        List<BasicImage> basicImagePSList = basicImageRepository.saveAll(basicImageList);

        // then
        assertThat(basicImagePSList.size()).isEqualTo(basicImageList.size());
        assertThat(basicImagePSList.get(0).getImageUrl()).isEqualTo(basicImageList.get(0).getImageUrl());
    }

    // 대표사진 URL 전체 조회
    @Test
    public void mFindAllImageUrlByStoreId() {
        // given
        String imageUrl = "imageUrl1.png";

        // when
        List<String> imageUrlList = basicImageRepository.mFindAllImageUrlByStoreId(storePS.getId());

        // then
        assertThat(imageUrlList.size()).isEqualTo(1);
        assertThat(imageUrlList.get(0)).isEqualTo(imageUrl);
    }

    // 대표사진 URL 1개 조회
    @Test
    public void mFindOneImageUrlByStoreId() {
        // given
        String imageUrl = "imageUrl1.png";

        // when
        String imageUrlResult = basicImageRepository.mFindOneImageUrlByStoreId(storePS.getId());

        // then
        assertThat(imageUrlResult).isEqualTo(imageUrl);
    }
}
