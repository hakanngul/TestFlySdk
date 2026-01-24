package com.testfly.sdk.manager;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.testfly.sdk.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initializeDriver(String browserType) {
        try {
            logger.info("=== Initializing Driver ===");

            // Clear any previous instances
            playwrightThreadLocal.remove();
            browserThreadLocal.remove();
            contextThreadLocal.remove();
            pageThreadLocal.remove();

            Playwright playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);

            Browser browser = createBrowser(playwright, browserType);
            browserThreadLocal.set(browser);

            BrowserContext context = browser.newContext();
            contextThreadLocal.set(context);

            Page page = context.newPage();

            // Set longer default timeout for headful mode
            int defaultTimeout = ConfigManager.get().isHeadless() ?
                ConfigManager.get().timeout() :
                ConfigManager.get().headfulTimeout();

            page.setDefaultTimeout(defaultTimeout);
            page.setDefaultNavigationTimeout(defaultTimeout);

            pageThreadLocal.set(page);

            logger.info("Driver initialized successfully:");
            logger.info("  Playwright: {}", playwrightThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Browser: {}", browserThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Context: {}", contextThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Page: {}", pageThreadLocal.get() != null ? "OK" : "NULL");
            logger.info("  Default Timeout: {}ms", defaultTimeout);
            logger.info("  Thread ID: {}", Thread.currentThread().threadId());
        } catch (Exception e) {
            logger.error("Error initializing driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize driver", e);
        }
    }

    private static Browser createBrowser(Playwright playwright, String browserType) {
        if ("chrome".equalsIgnoreCase(browserType) && !ConfigManager.get().isHeadless()) {
            logger.info("Launching Chrome (headful mode)");
            return playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false).setChannel("chrome")
            );
        }

        logger.info("Launching browser: {} (headless: {})", browserType, ConfigManager.get().isHeadless());

        return switch (browserType.toLowerCase()) {
            case "chrome", "chromium" -> playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(ConfigManager.get().isHeadless())
            );
            case "firefox" -> playwright.firefox().launch(
                    new BrowserType.LaunchOptions().setHeadless(ConfigManager.get().isHeadless())
            );
            case "webkit" -> playwright.webkit().launch(
                    new BrowserType.LaunchOptions().setHeadless(ConfigManager.get().isHeadless())
            );
            default -> throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        };
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

    public static Playwright getPlaywright() {
        return playwrightThreadLocal.get();
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

            Playwright playwright = playwrightThreadLocal.get();
            if (playwright != null) {
                playwright.close();
                playwrightThreadLocal.remove();
            }
        } catch (Exception e) {
            System.err.println("Error while quitting driver: " + e.getMessage());
        }
    }

    public static void clearAll() {
        playwrightThreadLocal.remove();
        browserThreadLocal.remove();
        contextThreadLocal.remove();
        pageThreadLocal.remove();
    }
}
