package com.gov.dashboard.service;

import com.gov.dashboard.entity.DistrictPerformance;
import com.gov.dashboard.repository.DistrictPerformanceRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvDataService {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataService.class);

    @Autowired
    private DistrictPerformanceRepository repository;

    @Value("${app.csv.directory:C:\\Users\\Lakshmi Makkena\\Desktop\\Gov Project\\Gov Data CSV's}")
    private String csvDirectory;

    @Transactional
    public void loadCsvData(String filePath) {
        try {
            logger.info("Loading CSV data from: {}", filePath);
            
            List<DistrictPerformance> records = new ArrayList<>();
            int batchSize = 1000;
            int totalRecords = 0;
            
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                // Skip header row
                String[] nextLine;
                boolean isFirstLine = true;
                
                while ((nextLine = reader.readNext()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Skip header
                    }
                    
                    if (nextLine.length >= 36) { // Ensure we have enough columns
                        DistrictPerformance record = mapRowToEntity(nextLine);
                        if (record != null) {
                            records.add(record);
                            
                            // Save in batches to reduce memory usage
                            if (records.size() >= batchSize) {
                                repository.saveAll(records);
                                totalRecords += records.size();
                                records.clear();
                                logger.debug("Saved batch of {} records, total: {}", batchSize, totalRecords);
                            }
                        }
                    }
                }
                
                // Save remaining records
                if (!records.isEmpty()) {
                    repository.saveAll(records);
                    totalRecords += records.size();
                }
            }
            
            logger.info("Successfully loaded {} records from CSV", totalRecords);
            
        } catch (IOException | CsvException e) {
            logger.error("Error loading CSV data from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load CSV data", e);
        }
    }

    public void loadAllCsvFiles() {
        try {
            Path directory = Paths.get(csvDirectory);
            if (!Files.exists(directory)) {
                logger.warn("CSV directory does not exist: {}", csvDirectory);
                return;
            }

            Files.list(directory)
                .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                .forEach(path -> {
                    try {
                        loadCsvData(path.toString());
                    } catch (Exception e) {
                        logger.error("Error loading file {}: {}", path, e.getMessage());
                    }
                });

        } catch (IOException e) {
            logger.error("Error reading CSV directory: {}", e.getMessage());
        }
    }

    private DistrictPerformance mapRowToEntity(String[] row) {
        try {
            DistrictPerformance entity = new DistrictPerformance();
            
            entity.setFinYear(safeString(row[0]));
            entity.setMonth(safeString(row[1]));
            entity.setStateCode(safeString(row[2]));
            entity.setStateName(safeString(row[3]));
            entity.setDistrictCode(safeString(row[4]));
            entity.setDistrictName(safeString(row[5]));
            entity.setApprovedLabourBudget(safeBigDecimal(row[6]));
            entity.setAverageWageRatePerDayPerPerson(safeBigDecimal(row[7]));
            entity.setAverageDaysOfEmploymentProvidedPerHousehold(safeBigDecimal(row[8]));
            entity.setDifferentlyAbledPersonsWorked(safeBigDecimal(row[9]));
            entity.setMaterialAndSkilledWages(safeBigDecimal(row[10]));
            entity.setNumberOfCompletedWorks(safeBigDecimal(row[11]));
            entity.setNumberOfGpsWithNilExp(safeBigDecimal(row[12]));
            entity.setNumberOfOngoingWorks(safeBigDecimal(row[13]));
            entity.setPersondaysOfCentralLiabilitySoFar(safeBigDecimal(row[14]));
            entity.setScPersondays(safeBigDecimal(row[15]));
            entity.setScWorkersAgainstActiveWorkers(safeBigDecimal(row[16]));
            entity.setStPersondays(safeBigDecimal(row[17]));
            entity.setStWorkersAgainstActiveWorkers(safeBigDecimal(row[18]));
            entity.setTotalAdmExpenditure(safeBigDecimal(row[19]));
            entity.setTotalExp(safeBigDecimal(row[20]));
            entity.setTotalHouseholdsWorked(safeBigDecimal(row[21]));
            entity.setTotalIndividualsWorked(safeBigDecimal(row[22]));
            entity.setTotalNoOfActiveJobCards(safeBigDecimal(row[23]));
            entity.setTotalNoOfActiveWorkers(safeBigDecimal(row[24]));
            entity.setTotalNoOfHhsCompleted100DaysOfWageEmployment(safeBigDecimal(row[25]));
            entity.setTotalNoOfJobcardsIssued(safeBigDecimal(row[26]));
            entity.setTotalNoOfWorkers(safeBigDecimal(row[27]));
            entity.setTotalNoOfWorksTakenup(safeBigDecimal(row[28]));
            entity.setWages(safeBigDecimal(row[29]));
            entity.setWomenPersondays(safeBigDecimal(row[30]));
            entity.setPercentOfCategoryBWorks(safeBigDecimal(row[31]));
            entity.setPercentOfExpenditureOnAgricultureAlliedWorks(safeBigDecimal(row[32]));
            entity.setPercentOfNrmExpenditure(safeBigDecimal(row[33]));
            entity.setPercentagePaymentsGeneratedWithin15Days(safeBigDecimal(row[34]));
            entity.setRemarks(safeString(row[35]));

            return entity;
            
        } catch (Exception e) {
            logger.error("Error mapping row to entity: {}", e.getMessage());
            return null;
        }
    }

    private String safeString(String value) {
        return (value != null && !value.trim().isEmpty() && !value.equals("NA")) ? value.trim() : null;
    }

    private BigDecimal safeBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("NA")) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Could not parse BigDecimal from: {}", value);
            return null;
        }
    }

    public boolean isDatabaseEmpty() {
        return repository.countAllRecords() == 0;
    }
}
