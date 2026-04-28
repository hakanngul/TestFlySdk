package com.testfly.sdk.reporting.listeners;

import com.testfly.sdk.core.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private final int maxRetry;
    private final boolean retryEnabled;

    public RetryAnalyzer() {
        this.retryEnabled = ConfigManager.get().isRetryEnabled();
        this.maxRetry = ConfigManager.get().retryMax();
    }

    @Override
    public boolean retry(ITestResult result) {
        if (!retryEnabled) {
            logger.debug("Retry is disabled. Skipping retry for: {}", result.getMethod().getMethodName());
            return false;
        }

        if (retryCount < maxRetry) {
            retryCount++;
            logger.warn("Retrying test: {} (attempt {}/{})",
                    result.getMethod().getMethodName(), retryCount + 1, maxRetry + 1);
            return true;
        }

        logger.warn("Retry exhausted for: {} after {} attempts",
                result.getMethod().getMethodName(), maxRetry + 1);
        return false;
    }
}