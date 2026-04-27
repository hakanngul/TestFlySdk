package com.testfly.sdk.core;

import com.microsoft.playwright.Playwright;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlaywrightManager {

    private static final Logger logger = LogManager.getLogger(PlaywrightManager.class);
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();

    private PlaywrightManager() {
    }

    public static Playwright getOrCreate() {
        Playwright pw = playwrightThreadLocal.get();
        if (pw == null) {
            logger.info("Creating new Playwright instance for thread: {}", Thread.currentThread().threadId());
            pw = Playwright.create();
            playwrightThreadLocal.set(pw);
        }
        return pw;
    }

    public static Playwright get() {
        return playwrightThreadLocal.get();
    }

    public static void dispose() {
        Playwright pw = playwrightThreadLocal.get();
        if (pw != null) {
            logger.info("Disposing Playwright instance for thread: {}", Thread.currentThread().threadId());
            pw.close();
            playwrightThreadLocal.remove();
        }
    }
}
