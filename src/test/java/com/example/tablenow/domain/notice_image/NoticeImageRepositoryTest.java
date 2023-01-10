package com.example.tablenow.domain.notice_image;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.notice.NoticeRepository;

@ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
@DataJpaTest // DB 관련 컴포넌트만 메모리에 로딩 (예. Repository)
public class NoticeImageRepositoryTest {

    @Autowired
    private NoticeImageRepository noticeImageRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    Notice noticePS; // 첨부사진 등록한 Notice 데이터

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        // 1. Notice 등록
        noticePS = noticeRepository
                .save(Notice.builder().title("알림제목").content("알림내용").holidayStartDate("")
                        .holidayEndDate("").build());

        // 2. NoticeImage 전체등록
        List<NoticeImage> noticeImageList = new ArrayList<>();
        NoticeImage noticeImage = NoticeImage.builder().imageUrl("imageUrl1.png").notice(noticePS).build();
        noticeImageList.add(noticeImage);
        noticeImageRepository.saveAll(noticeImageList);
    }

    // 첨부사진 전체등록
    @Test
    public void saveAll() {
        // given
        List<NoticeImage> noticeImageList = new ArrayList<>();
        NoticeImage noticeImage = NoticeImage.builder().imageUrl("imageUrl2.png").notice(noticePS).build();
        noticeImageList.add(noticeImage);

        // when
        List<NoticeImage> noticeImagePSList = noticeImageRepository.saveAll(noticeImageList);

        // then
        assertThat(noticeImagePSList.size()).isEqualTo(noticeImageList.size());
        assertThat(noticeImagePSList.get(0).getImageUrl()).isEqualTo(noticeImageList.get(0).getImageUrl());
    }

    // 알림 첨부사진 URL 리스트
    @Test
    public void mFindAllImageUrlByNoticeId() {
        // given
        String imageUrl = "imageUrl1.png";

        // when
        List<String> imageUrlList = noticeImageRepository.mFindAllImageUrlByNoticeId(noticePS.getId());

        // then
        assertThat(imageUrlList.size()).isEqualTo(1);
        assertThat(imageUrlList.get(0)).isEqualTo(imageUrl);
    }
}
