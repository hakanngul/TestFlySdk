package com.testfly.sdk.bdd.hooks;

import com.microsoft.playwright.Page;
import com.testfly.sdk.core.BrowserManager;
import com.testfly.sdk.core.ConfigManager;
import com.testfly.sdk.core.LogManager;
import com.testfly.sdk.utils.StringUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebHooks {

    private static final Logger logger = LogManager.getLogger(WebHooks.class);
    private static final String ALLURE_RESULTS = "target/allure-results";

    @Before(value = "@ui or @web", order = 1)
    public void beforeScenario(Scenario scenario) {
        logger.info("Starting scenario: {}", scenario.getName());
        String browserType = ConfigManager.get().browser();
        BrowserManager.initializeDriver(browserType);
        BrowserManager.startTracing();
    }

    @After(value = "@ui or @web", order = 1)
    public void afterScenario(Scenario scenario) {
        logger.info("=== After Scenario Hook: {} ===", scenario.getName());

        if (scenario.isFailed()) {
            handleFailedScenario(scenario);
        } else {
            handlePassedScenario();
        }

        BrowserManager.quitDriver();
    }

    private void handleFailedScenario(Scenario scenario) {
        logger.error("Scenario failed: {}", scenario.getName());

        BrowserManager.stopTracing(scenario.getName());

        // Screenshot (tek ekleme)
        takeScreenshotBytes(scenario);

        // Video (tek ekleme)
        BrowserManager.saveVideo(scenario.getName()).ifPresent(videoPath -> {
            attachVideoToAllure(scenario, videoPath);
        });
    }

    private void handlePassedScenario() {
        BrowserManager.stopAndDiscardTracing();
        BrowserManager.discardVideo();
        logger.info("Scenario passed");
    }

    /**
     * Video'yu Allure'a TEK SEFERDE ekler.
     */
    private void attachVideoToAllure(Scenario scenario, Path videoPath) {
        try {
            long fileSize = Files.size(videoPath);
            logger.info("Video size: {} bytes", fileSize);

            // Tek ekleme: Allure.addAttachment ile
            byte[] videoBytes = Files.readAllBytes(videoPath);
            Allure.addAttachment(
                    scenario.getName() + "_video.mp4",
                    "video/mp4",
                    new ByteArrayInputStream(videoBytes),
                    ".mp4");

            scenario.log("Video saved: " + videoPath.toAbsolutePath());
            logger.info("Video attached to Allure: {}", videoPath.getFileName());

        } catch (Exception e) {
            logger.error("Failed to attach video: {}", e.getMessage(), e);
        }
    }

    private byte[] takeScreenshotBytes(Scenario scenario) {
        try {
            Page page = BrowserManager.getPage();
            if (page == null || page.isClosed()) {
                logger.warn("Page is null or closed, cannot take screenshot");
                return null;
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = StringUtils.replace(scenario.getName(), " ", "_") + "_" + timestamp;

            Files.createDirectories(Paths.get("target/screenshots"));

            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("target/screenshots/" + fileName + ".png")));

            // Tek ekleme: scenario.attach (Allure Cucumber plugin otomatik alır)
            scenario.attach(screenshotBytes, "image/png", fileName + ".png");

            logger.info("Screenshot saved: {}", fileName);
            return screenshotBytes;

        } catch (Exception e) {
            logger.error("Screenshot failed: {}", e.getMessage(), e);
            return null;
        }
    }
}