package com.gov.dashboard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CsvDataService csvDataService;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @PostConstruct
    public void initializeData() {
        try {
            if (csvDataService.isDatabaseEmpty()) {
                logger.info("Database is empty, loading CSV data...");
                csvDataService.loadAllCsvFiles();
                // Clear any stale caches if present
                try {
                    if (cacheManager != null) {
                        cacheManager.getCacheNames().forEach(name -> {
                            try {
                                var cache = cacheManager.getCache(name);
                                if (cache != null) {
                                    cache.clear();
                                }
                            } catch (Exception ignored) {}
                        });
                    }
                } catch (Exception e) {
                    logger.warn("Unable to clear caches after data load: {}", e.getMessage());
                }
                logger.info("Data initialization completed successfully");
            } else {
                logger.info("Database already contains data, skipping initialization");
            }
        } catch (Exception e) {
            logger.error("Error during data initialization: {}", e.getMessage());
        }
    }
}