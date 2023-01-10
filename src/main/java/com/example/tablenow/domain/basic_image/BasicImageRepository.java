package com.example.tablenow.domain.basic_image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasicImageRepository extends JpaRepository<BasicImage, Long> {

    @Query(value = "SELECT imageUrl FROM basic_image WHERE storeId = :storeId", nativeQuery = true)
    public List<String> mFindAllImageUrlByStoreId(@Param("storeId") Long storeId);

    @Query(value = "SELECT imageUrl FROM basic_image WHERE storeId = :storeId GROUP BY storeId", nativeQuery = true)
    public String mFindOneImageUrlByStoreId(@Param("storeId") Long storeId);

    @Query(value = "SELECT COUNT(*) FROM basic_image WHERE storeId = :storeId", nativeQuery = true)
    public int mCountAllByStoreId(@Param("storeId") Long storeId);

    // findBy 규칙
    // SELECT * FROM basic_image WHERE storeId = ?
    public List<BasicImage> findAllByStoreId(Long storeId);

    // deleteBy 규칙
    // DELETE FROM basic_image WHERE imageUrl = ?
    public void deleteByImageUrl(String imageUrl);

    // DELETE FROM basic_image WHERE storeId = ?
    public void deleteAllByStoreId(Long storeId);
}
