package com.example.tablenow.domain.holidays;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidaysRepository extends JpaRepository<Holidays, Long> {

	@Query(value = "SELECT holidays FROM holidays WHERE storeId = :storeId", nativeQuery = true)
	public String mFindHolidaysByStoreId(@Param("storeId") Long storeId);

	// findBy 규칙
	// SELECT * FROM holidays WHERE storeId = ?
	public Holidays findByStoreId(Long storeId);

	// DELETE FROM holidays WHERE storeId = ?
	public void deleteByStoreId(Long storeId);
}
