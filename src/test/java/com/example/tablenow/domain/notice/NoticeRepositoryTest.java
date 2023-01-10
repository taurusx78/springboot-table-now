package com.example.tablenow.domain.notice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.store.StoreRepository;

@ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
@DataJpaTest // DB 관련 컴포넌트만 메모리에 로딩 (예. Repository)
public class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private StoreRepository storeRepository;

    Long noticeId; // 알림 id
    Store storePS; // 알림 등록한 Store 데이터

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        String title = "알림제목1";
        String content = "알림내용1";
        String holidayStartDate = "2022-11-12";
        String holidayEndDate = "2022-11-14";

        storePS = storeRepository
                .save(Store.builder().name("매장명").category("카테고리").phone("02-1111-1111").address("도로명주소")
                        .jibunAddress("지번주소").latitude(37.1).longitude(127.1).description("매장소개").website("").build());

        Notice noticePS = noticeRepository
                .save(Notice.builder().title(title).content(content).holidayStartDate(holidayStartDate)
                        .holidayEndDate(holidayEndDate).store(storePS).build());
        noticeId = noticePS.getId();
    }

    // 알림등록
    @Test
    public void save() {
        String title = "알림제목2";
        String content = "알림내용2";
        String holidayStartDate = "2022-11-15";
        String holidayEndDate = "2022-11-17";
        Notice notice = Notice.builder().title(title).content(content).holidayStartDate(holidayStartDate)
                .holidayEndDate(holidayEndDate).store(storePS).build();

        // when
        Notice noticePS = noticeRepository.save(notice);

        // then
        assertThat(noticePS.getTitle()).isEqualTo(title);
        assertThat(noticePS.getContent()).isEqualTo(content);
        assertThat(noticePS.getHolidayStartDate()).isEqualTo(holidayStartDate);
        assertThat(noticePS.getHolidayEndDate()).isEqualTo(holidayEndDate);
        assertThat(noticePS.getStore()).isEqualTo(storePS);
    }

    // 알림 전체조회
    @Test
    public void findAllByStoreId() {
        // given

        // when
        List<Notice> noticePSList = noticeRepository.findAllByStoreId(storePS.getId());

        // then
        assertThat(noticePSList.size()).isEqualTo(1);
        assertThat(noticePSList.get(0).getId()).isEqualTo(noticeId);
    }

    // 알림 데이터 영속화
    @Test
    public void findById() {
        // given

        // when
        Optional<Notice> noticeOP1 = noticeRepository.findById(noticeId);
        Optional<Notice> noticeOP2 = noticeRepository.findById(noticeId + 1L);

        // then
        assertThat(noticeOP1.get().getId()).isEqualTo(noticeId);
        assertThat(noticeOP2).isEmpty();
    }

    // 알림 ID 전체조회
    @Test
    public void mFindAllIdByStoreId() {
        // given

        // when
        List<Long> noticeIdList = noticeRepository.mFindAllIdByStoreId(storePS.getId());

        // then
        assertThat(noticeIdList.size()).isEqualTo(1);
        assertThat(noticeIdList.get(0)).isEqualTo(noticeId);
    }
}
