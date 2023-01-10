package com.example.tablenow.domain.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TablesRepository extends JpaRepository<Tables, Long> {

	@Query(value = "SELECT allTableCount FROM tables WHERE storeId = :storeId", nativeQuery = true)
	public int mFindAllTableCountByStoreId(@Param("storeId") Long storeId);

	// findBy 규칙
	// SELECT * FROM tables WHERE storeId = ?
	public Tables findByStoreId(Long storeId);

	// deleteBy 규칙
	// DELETE FROM tables WHERE storeId = ?
	public void deleteByStoreId(Long storeId);
}
