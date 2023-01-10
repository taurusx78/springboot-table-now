package com.example.tablenow.domain.today_hours;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodayHoursRepository extends JpaRepository<TodayHours, Long> {

	@Query(value = "SELECT today FROM today_hours WHERE storeId = :storeId", nativeQuery = true)
	public Date mFindTodayByStoreId(@Param("storeId") Long storeId);

	// findBy 규칙
	// SELECT * FROM today_hours WHERE storeId = ?
	public TodayHours findByStoreId(Long storeId);

	// deleteBy 규칙
	// DELETE FROM today_hours WHERE storeId = ?
	public void deleteByStoreId(Long storeId);
}
