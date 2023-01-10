package com.example.tablenow.domain.image_modified_date;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageModifiedDateRepository extends JpaRepository<ImageModifiedDate, Long> {

    @Modifying
    @Query(value = "UPDATE image_modified_date SET basicModified = :now WHERE storeId = :storeId", nativeQuery = true)
    public void mUpdateBasicModified(@Param("storeId") Long storeId, @Param("now") LocalDate now);

    @Modifying
    @Query(value = "UPDATE image_modified_date SET insideModified = :now WHERE storeId = :storeId", nativeQuery = true)
    public void mUpdateInsideModified(@Param("storeId") Long storeId, @Param("now") LocalDate now);

    @Modifying
    @Query(value = "UPDATE image_modified_date SET menuModified = :now WHERE storeId = :storeId", nativeQuery = true)
    public void mUpdateMenuModified(@Param("storeId") Long storeId, @Param("now") LocalDate now);

    @Query(value = "SELECT basicModified FROM image_modified_date WHERE storeId = :storeId", nativeQuery = true)
    public Date mFindBasicModifiedByStoreId(@Param("storeId") Long storeId);

    @Query(value = "SELECT insideModified FROM image_modified_date WHERE storeId = :storeId", nativeQuery = true)
    public Date mFindInsideModifiedByStoreId(@Param("storeId") Long storeId);

    @Query(value = "SELECT menuModified FROM image_modified_date WHERE storeId = :storeId", nativeQuery = true)
    public Date mFindMenuModifiedByStoreId(@Param("storeId") Long storeId);
}
