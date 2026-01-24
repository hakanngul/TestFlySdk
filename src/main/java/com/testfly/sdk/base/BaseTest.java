package com.testfly.sdk.base;

import com.microsoft.playwright.Page;
import com.testfly.sdk.actions.IPageContext;
import com.testfly.sdk.actions.IWebActions;
import com.testfly.sdk.config.ConfigManager;
import com.testfly.sdk.context.ScenarioContext;
import com.testfly.sdk.manager.DriverManager;
import com.testfly.sdk.manager.LogManager; // Senin LogManager'ın
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public abstract class BaseTest implements IWebActions {

    // Protected to allow each test class to log its own messages
    protected final Logger logger = LogManager.getLogger(this.getClass());


    /**
     * For easy access to Page object in test classes.
     * Instead of writing new LoginPage(getPage()).
     */
    @Override
    public Page getPage() {
        return DriverManager.getPage();
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }


    /**
     * To intentionally skip a test.
     * Will appear as "Skipped" in the report.
     */
    protected void skipTest(String reason) {
        logger.warn("Skipping test. Reason: {}", reason);
        throw new SkipException(reason);
    }

    /**
     * To manually fail a test.
     */
    protected void failTest(String reason) {
        logger.error("Failing test manually. Reason: {}", reason);
        throw new AssertionError(reason); // RuntimeException yerine AssertionError daha uygundur
    }

    /**
     * Hard Wait (Should only be used when absolutely necessary)
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
