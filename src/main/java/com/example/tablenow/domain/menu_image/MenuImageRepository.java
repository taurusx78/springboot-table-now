package com.example.tablenow.domain.menu_image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuImageRepository extends JpaRepository<MenuImage, Long> {

	@Query(value = "SELECT imageUrl FROM menu_image WHERE storeId = :storeId", nativeQuery = true)
	public List<String> mFindAllImageUrlByStoreId(@Param("storeId") Long storeId);

	@Query(value = "SELECT COUNT(*) FROM menu_image WHERE storeId = :storeId", nativeQuery = true)
	public int mCountAllByStoreId(@Param("storeId") Long storeId);

	// findBy 규칙
	// SELECT * FROM menu_image WHERE storeId = ?
	public List<MenuImage> findAllByStoreId(Long storeId);

	// deleteBy 규칙
	// DELETE FROM menu_image WHERE imageUrl = ?
	public void deleteByImageUrl(String imageUrl);

	// DELETE FROM menu_image WHERE storeId = ?
	public void deleteAllByStoreId(Long storeId);
}
