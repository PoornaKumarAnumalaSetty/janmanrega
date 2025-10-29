package com.gov.dashboard.repository;

import com.gov.dashboard.entity.DistrictPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictPerformanceRepository extends JpaRepository<DistrictPerformance, Long> {

    List<DistrictPerformance> findByDistrictNameOrderByFinYearDesc(String districtName);

    @Query("SELECT DISTINCT d.districtName FROM DistrictPerformance d ORDER BY d.districtName")
    List<String> findDistinctDistrictNames();

    @Query("SELECT d FROM DistrictPerformance d WHERE d.districtName = ?1 ORDER BY d.finYear DESC")
    List<DistrictPerformance> findPerformanceByDistrictOrderByYear(String districtName);

    @Query("SELECT d FROM DistrictPerformance d WHERE d.districtName = ?1 AND d.finYear = ?2")
    List<DistrictPerformance> findByDistrictNameAndFinYear(String districtName, String finYear);

    @Query("SELECT COUNT(d) FROM DistrictPerformance d")
    long countAllRecords();

    @Query("SELECT MAX(d.finYear) FROM DistrictPerformance d")
    String findMaxFinYear();
}