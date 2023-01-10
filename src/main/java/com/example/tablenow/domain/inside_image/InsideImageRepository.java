package com.example.tablenow.domain.inside_image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InsideImageRepository extends JpaRepository<InsideImage, Long> {

	@Query(value = "SELECT imageUrl FROM inside_image WHERE storeId = :storeId", nativeQuery = true)
	public List<String> mFindAllImageUrlByStoreId(@Param("storeId") Long storeId);

	@Query(value = "SELECT COUNT(*) FROM inside_image WHERE storeId = :storeId", nativeQuery = true)
	public int mCountAllByStoreId(@Param("storeId") Long storeId);

	// findBy 규칙
	// SELECT * FROM inside_image WHERE storeId = ?
	public List<InsideImage> findAllByStoreId(Long storeId);

	// deleteBy 규칙
	// DELETE FROM inside_image WHERE imageUrl = ?
	public void deleteByImageUrl(String imageUrl);

	// DELETE FROM inside_image WHERE storeId = ?
	public void deleteAllByStoreId(Long storeId);
}
