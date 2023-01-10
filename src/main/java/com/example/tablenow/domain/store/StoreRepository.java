package com.example.tablenow.domain.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Long> {

	@Query(value = "SELECT COUNT(*) FROM store WHERE businessRegNum = :businessRegNum", nativeQuery = true)
	public int mCheckExist(@Param("businessRegNum") String businessRegNum);

	// 사용자의 매장이 맞는지 확인
	@Query(value = "SELECT COUNT(*) FROM store WHERE id = :storeId AND userId = :userId", nativeQuery = true)
	public int mCheckStoreManager(@Param("storeId") Long storeId, @Param("userId") Long userId);

	@Query(value = "SELECT id FROM store WHERE userId = :userId", nativeQuery = true)
	public List<Long> mFindAllIdByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT name FROM store WHERE id = :storeId", nativeQuery = true)
	public String mFindNameByStoreId(@Param("storeId") Long storeId);
}
