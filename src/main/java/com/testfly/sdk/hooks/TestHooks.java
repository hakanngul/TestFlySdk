package com.testfly.sdk.hooks;

import com.microsoft.playwright.Page;
import com.testfly.sdk.manager.DriverManager;
import com.testfly.sdk.manager.LogManager;
import com.testfly.sdk.utils.StringUtils;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;

public class TestHooks {

    private static final Logger logger = LogManager.getLogger(TestHooks.class);

    @Before(value = "@ui or @web", order = 1)
    public void beforeScenario(Scenario scenario) {
        logger.info("Starting scenario: {}", scenario.getName());

        String browserType = "chrome";
        DriverManager.initializeDriver(browserType);

        logger.info("Browser initialized for thread: {}", Thread.currentThread().threadId());
    }

    @Before(value = "@ui or @web", order = 2)
    public void logScenarioStart(Scenario scenario) {
        scenario.log("Scenario started with thread ID: " + Thread.currentThread().threadId());
    }

    @After(value = "@ui or @web", order = 1)
    public void afterScenario(Scenario scenario) {
        logger.info("=== After Scenario Hook Started for: {} ===", scenario.getName());

        if (scenario.isFailed()) {
            logger.error("Scenario failed: {}", scenario.getName());
            byte[] screenshot = takeScreenshotBytes(scenario);
            if (screenshot != null) {
                Allure.addAttachment(scenario.getName() + "_screenshot.png", new ByteArrayInputStream(screenshot));
            }
        } else {
            logger.info("Scenario passed: {}", scenario.getName());
        }

        DriverManager.quitDriver();
    }

    @After(value = "@ui or @web", order = 2)
    public void cleanup() {
        logger.info("=== Cleanup Hook Started ===");
        logger.info("=== Cleanup Hook Finished ===");
    }

    private byte[] takeScreenshotBytes(Scenario scenario) {
        try {
            Page page = DriverManager.getPage();
            if (page != null) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String fileName = StringUtils.replace(scenario.getName(), " ", "_") + "_" + timestamp;
                String screenshotPath = "target/screenshots/" + fileName + ".png";

                java.nio.file.Files.createDirectories(Paths.get("target/screenshots"));

                byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(screenshotPath)));

                // Attach to Cucumber report (shows in Allure via Cucumber adapter)
                scenario.attach(screenshotBytes, "image/png", fileName + ".png");

                // Directly copy to Allure results directory
                try {
                    java.nio.file.Files.write(
                        Paths.get("target/allure-results/" + fileName + ".png"),
                        screenshotBytes
                    );
                    logger.info("Screenshot also copied to Allure results: {}", fileName + ".png");
                } catch (Exception ex) {
                    logger.warn("Failed to copy screenshot to Allure results: {}", ex.getMessage());
                }

                logger.info("Screenshot saved: {}", screenshotPath);
                return screenshotBytes;
            } else {
                logger.warn("Cannot take screenshot: Page is null");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot for scenario: {}", scenario.getName(), e);
            return null;
        }
    }
}
