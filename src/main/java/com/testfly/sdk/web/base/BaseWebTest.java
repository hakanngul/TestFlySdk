package com.testfly.sdk.web.base;

import com.microsoft.playwright.Page;
import com.testfly.sdk.web.actions.WebActions;
import com.testfly.sdk.core.BrowserManager;
import com.testfly.sdk.core.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.SkipException;

public abstract class BaseWebTest implements WebActions {

    protected final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public Page getPage() {
        return BrowserManager.getPage();
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    protected void skipTest(String reason) {
        logger.warn("Skipping test. Reason: {}", reason);
        throw new SkipException(reason);
    }

    protected void failTest(String reason) {
        logger.error("Failing test manually. Reason: {}", reason);
        throw new AssertionError(reason);
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
