package com.example.tablenow.domain.notice_image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

    @Query(value = "SELECT imageUrl FROM notice_image WHERE noticeId = :noticeId", nativeQuery = true)
    public List<String> mFindAllImageUrlByNoticeId(@Param("noticeId") Long noticeId);

    @Query(value = "SELECT COUNT(*) FROM menu_image WHERE storeId = :storeId", nativeQuery = true)
    public int mCountAllByStoreId(@Param("storeId") Long storeId);

    // deleteBy 규칙
    // DELETE FROM notice_image WHERE imageUrl = ?
    public void deleteByImageUrl(String imageUrl);

    // DELETE FROM notice_image WHERE noticeId = ?
    public void deleteAllByNoticeId(Long noticeId);
}
