package com.example.tablenow.domain.hours;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HoursRepository extends JpaRepository<Hours, Long> {

    // findBy 규칙
    // SELECT * FROM hours WHERE dayType = ? AND storeId = ?
    public Hours findByDayTypeAndStoreId(int dayType, Long storeId);

    // SELECT * FROM hours WHERE storeId = ?
    public List<Hours> findAllByStoreId(Long storeId);

    // deleteBy 규칙
    // DELETE FROM hours WHERE storeId = ?
    public void deleteByStoreId(Long storeId);
}
