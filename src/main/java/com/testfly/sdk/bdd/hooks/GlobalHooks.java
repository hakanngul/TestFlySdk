package com.testfly.sdk.bdd.hooks;

import com.testfly.sdk.core.PlaywrightManager;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Global lifecycle hooks shared across all test types.
 * Handles suite-level setup (directories) and teardown (Playwright disposal).
 */
public class GlobalHooks {

    private static final Logger logger = LogManager.getLogger(GlobalHooks.class);

    @BeforeAll
    public static void beforeAll() {
        logger.info("========================================");
        logger.info("   TestFly SDK Test Suite Starting");
        logger.info("========================================");

        createRequiredDirectories();
    }

    @AfterAll
    public static void afterAll() {
        logger.info("========================================");
        logger.info("   TestFly SDK Test Suite Finished");
        logger.info("========================================");

        PlaywrightManager.dispose();
    }

    private static void createRequiredDirectories() {
        String[] dirs = {
                "target/screenshots",
                "target/allure-results",
                "target/extent-reports",
                "target/traces"
        };

        for (String dir : dirs) {
            try {
                Files.createDirectories(Paths.get(dir));
                logger.debug("Directory ready: {}", dir);
            } catch (Exception e) {
                logger.warn("Could not create directory {}: {}", dir, e.getMessage());
            }
        }
    }
}