package com.testfly.sdk.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.testfly.sdk.exceptions.BrowserInitializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

public class BrowserManager {

    private static final Logger logger = LogManager.getLogger(BrowserManager.class);
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    private BrowserManager() {
    }

    public static void initializeDriver(String browserType) {
        try {
            logger.info("=== Initializing Browser ===");

            browserThreadLocal.remove();
            contextThreadLocal.remove();
            pageThreadLocal.remove();

            Playwright playwright = PlaywrightManager.getOrCreate();

            Browser browser = createBrowser(playwright, browserType);
            browserThreadLocal.set(browser);

            BrowserContext context = browser.newContext();
            contextThreadLocal.set(context);

            Page page = context.newPage();

            int defaultTimeout = ConfigManager.get().isHeadless() ?
                ConfigManager.get().timeout() :
                ConfigManager.get().headfulTimeout();

            page.setDefaultTimeout(defaultTimeout);
            page.setDefaultNavigationTimeout(defaultTimeout);

            pageThreadLocal.set(page);

            logger.info("Browser initialized successfully:");
            logger.info("  Browser: {}", browserThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Context: {}", contextThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Page: {}", pageThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Default Timeout: {}ms", defaultTimeout);
            logger.info("  Thread ID: {}", Thread.currentThread().threadId());
        } catch (Exception e) {
            logger.error("Error initializing browser: {}", e.getMessage(), e);
            throw new BrowserInitializationException("Failed to initialize browser", e);
        }
    }

    private static Browser createBrowser(Playwright playwright, String browserType) {
        boolean headless = ConfigManager.get().isHeadless();
        logger.info("Launching browser: {} (headless: {})", browserType, headless);
        return BrowserFactory.launch(playwright, browserType, headless);
    }

    public static Page getPage() {
        return pageThreadLocal.get();
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    public static Browser getBrowser() {
        return browserThreadLocal.get();
    }

    public static void quitDriver() {
        try {
            Page page = pageThreadLocal.get();
            if (page != null) {
                page.close();
                pageThreadLocal.remove();
            }

            BrowserContext context = contextThreadLocal.get();
            if (context != null) {
                context.close();
                contextThreadLocal.remove();
            }

            Browser browser = browserThreadLocal.get();
            if (browser != null) {
                browser.close();
                browserThreadLocal.remove();
            }
        } catch (Exception e) {
            System.err.println("Error while quitting browser: " + e.getMessage());
        }
    }

    public static void startTracing() {
        if (!ConfigManager.get().isTraceEnabled()) {
            return;
        }
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
            logger.info("Trace recording started for thread: {}", Thread.currentThread().threadId());
        }
    }

    public static void stopTracing(String testName) {
        if (!ConfigManager.get().isTraceEnabled()) {
            return;
        }
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            try {
                String tracePath = ConfigManager.get().tracePath();
                Files.createDirectories(Paths.get(tracePath));

                String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
                String filePath = tracePath + "/" + safeName + "_" + System.currentTimeMillis() + ".zip";

                context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get(filePath)));
                logger.info("Trace saved: {}", filePath);
            } catch (Exception e) {
                logger.warn("Failed to save trace for '{}': {}", testName, e.getMessage());
            }
        }
    }

    public static void stopAndDiscardTracing() {
        if (!ConfigManager.get().isTraceEnabled()) {
            return;
        }
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            try {
                context.tracing().stop();
                logger.debug("Trace discarded for thread: {}", Thread.currentThread().threadId());
            } catch (Exception e) {
                logger.debug("Failed to discard trace: {}", e.getMessage());
            }
        }
    }

    public static void clearAll() {
        browserThreadLocal.remove();
        contextThreadLocal.remove();
        pageThreadLocal.remove();
    }
}
