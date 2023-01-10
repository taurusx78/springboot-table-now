package com.example.tablenow.domain.notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query(value = "SELECT id FROM notice WHERE storeId = :storeId", nativeQuery = true)
	public List<Long> mFindAllIdByStoreId(@Param("storeId") Long storeId);

	// findBy 규칙
	// SELECT * FROM notice WHERE storeId = ?
	public List<Notice> findAllByStoreId(Long storeId);

	// deleteBy 규칙
	// DELETE FROM notice WHERE storeId = ?
	public void deleteAllByStoreId(Long storeId);
}
