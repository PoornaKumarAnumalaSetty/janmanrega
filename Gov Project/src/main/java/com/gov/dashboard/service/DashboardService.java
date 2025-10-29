package com.gov.dashboard.service;

import com.gov.dashboard.entity.DistrictPerformance;
import com.gov.dashboard.repository.DistrictPerformanceRepository;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private DistrictPerformanceRepository repository;

    // Do NOT cache districts list to avoid caching empty results at startup
    public List<String> findAllDistricts() {
        logger.info("Fetching all districts");
        return repository.findDistinctDistrictNames();
    }

    public String findLatestFinancialYear() {
        try {
            String maxYear = repository.findMaxFinYear();
            return maxYear != null ? maxYear : "N/A";
        } catch (Exception e) {
            logger.warn("Unable to determine latest financial year: {}", e.getMessage());
            return "N/A";
        }
    }

    @Cacheable(value = "district-performance", key = "#districtName")
    public List<DistrictPerformance> findPerformanceByDistrict(String districtName) {
        logger.info("Fetching performance data for district: {}", districtName);
        return repository.findPerformanceByDistrictOrderByYear(districtName);
    }

    @Cacheable(value = "dashboard-summary", key = "#districtName")
    public Map<String, Object> getDashboardSummary(String districtName) {
        logger.info("Generating dashboard summary for district: {}", districtName);

        List<DistrictPerformance> performances = findPerformanceByDistrict(districtName);
        Map<String, Object> summary = new HashMap<>();

        if (performances.isEmpty()) {
            return summary;
        }

        // Get latest year data
        DistrictPerformance latest = performances.get(0);

        // Calculate year-over-year trends
        Map<String, Object> trends = calculateTrends(performances);

        // Key metrics
        summary.put("districtName", districtName);
        summary.put("latestYear", latest.getFinYear());
        summary.put("latestMonth", latest.getMonth());
        summary.put("totalHouseholdsWorked", latest.getTotalHouseholdsWorked());
        summary.put("totalIndividualsWorked", latest.getTotalIndividualsWorked());
        summary.put("averageDaysOfEmployment", latest.getAverageDaysOfEmploymentProvidedPerHousehold());
        summary.put("averageWageRate", latest.getAverageWageRatePerDayPerPerson());
        summary.put("totalExpenditure", latest.getTotalExp());
        summary.put("totalWages", latest.getWages());
        summary.put("womenPersondays", latest.getWomenPersondays());
        summary.put("completedWorks", latest.getNumberOfCompletedWorks());
        summary.put("ongoingWorks", latest.getNumberOfOngoingWorks());
        summary.put("activeJobCards", latest.getTotalNoOfActiveJobCards());
        summary.put("activeWorkers", latest.getTotalNoOfActiveWorkers());
        summary.put("hundredDaysCompleted", latest.getTotalNoOfHhsCompleted100DaysOfWageEmployment());

        // Trends
        summary.put("trends", trends);

        // Chart data for person days
        summary.put("chartData", generateChartData(performances));

        return summary;
    }

    private Map<String, Object> calculateTrends(List<DistrictPerformance> performances) {
        Map<String, Object> trends = new HashMap<>();

        if (performances.size() < 2) {
            return trends;
        }

        DistrictPerformance current = performances.get(0);
        DistrictPerformance previous = performances.get(1);

        // Calculate percentage changes
        trends.put("householdsChange", calculatePercentageChange(
                previous.getTotalHouseholdsWorked(),
                current.getTotalHouseholdsWorked()
        ));

        trends.put("individualsChange", calculatePercentageChange(
                previous.getTotalIndividualsWorked(),
                current.getTotalIndividualsWorked()
        ));

        trends.put("expenditureChange", calculatePercentageChange(
                previous.getTotalExp(),
                current.getTotalExp()
        ));

        trends.put("wagesChange", calculatePercentageChange(
                previous.getWages(),
                current.getWages()
        ));

        trends.put("employmentDaysChange", calculatePercentageChange(
                previous.getAverageDaysOfEmploymentProvidedPerHousehold(),
                current.getAverageDaysOfEmploymentProvidedPerHousehold()
        ));

        return trends;
    }

    private Map<String, Object> calculatePercentageChange(BigDecimal previous, BigDecimal current) {
        Map<String, Object> change = new HashMap<>();

        if (previous == null || current == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            change.put("percentage", BigDecimal.ZERO);
            change.put("direction", "neutral");
            return change;
        }

        BigDecimal percentage = current.subtract(previous)
                .divide(previous, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        change.put("percentage", percentage.abs());
        change.put("direction", percentage.compareTo(BigDecimal.ZERO) > 0 ? "up" : "down");

        return change;
    }

    private Map<String, Object> generateChartData(List<DistrictPerformance> performances) {
        Map<String, Object> chartData = new HashMap<>();

        // Get last 5 years of data
        List<DistrictPerformance> lastFiveYears = performances.stream()
                .limit(5)
                .collect(Collectors.toList());

        List<String> years = lastFiveYears.stream()
                .map(DistrictPerformance::getFinYear)
                .collect(Collectors.toList());

        List<BigDecimal> personDays = lastFiveYears.stream()
                .map(p -> p.getTotalIndividualsWorked() != null ? p.getTotalIndividualsWorked() : BigDecimal.ZERO)
                .collect(Collectors.toList());

        chartData.put("years", years);
        chartData.put("personDays", personDays);

        return chartData;
    }

    public byte[] generateChartImage(String districtName) {
        try {
            List<DistrictPerformance> performances = findPerformanceByDistrict(districtName);
            Map<String, Object> chartData = generateChartData(performances);

            CategoryChart chart = new CategoryChartBuilder()
                    .width(800)
                    .height(400)
                    .title("Person Days Employment - " + districtName)
                    .xAxisTitle("Financial Year")
                    .yAxisTitle("Person Days")
                    .build();

            @SuppressWarnings("unchecked")
            List<String> years = (List<String>) chartData.get("years");
            @SuppressWarnings("unchecked")
            List<BigDecimal> personDays = (List<BigDecimal>) chartData.get("personDays");

            List<Double> personDaysDouble = personDays.stream()
                    .map(BigDecimal::doubleValue)
                    .collect(Collectors.toList());

            chart.addSeries("Person Days", years, personDaysDouble);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);
            return outputStream.toByteArray();

        } catch (IOException e) {
            logger.error("Error generating chart image: {}", e.getMessage());
            return new byte[0];
        }
    }
}