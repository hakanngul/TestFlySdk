package com.testfly.sdk.reporting.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private final int maxRetry;

    public RetryAnalyzer() {
        this.maxRetry = com.testfly.sdk.core.ConfigManager.get().retryMax();
    }

    @Override
    public boolean retry(ITestResult result) {
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
