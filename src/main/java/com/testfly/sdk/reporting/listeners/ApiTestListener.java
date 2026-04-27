package com.testfly.sdk.reporting.listeners;

import com.testfly.sdk.api.engine.ApiResponse;
import com.testfly.sdk.core.ApiManager;
import com.testfly.sdk.core.PlaywrightManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ApiTestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(ApiTestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("=== API Test Suite Started: {} ===", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== API Test Suite Finished: {} ===", context.getName());
        PlaywrightManager.dispose();
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("API Test Started: {}", result.getMethod().getMethodName());
        Allure.step("API Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("API Test Passed: {}", result.getMethod().getMethodName());
        Allure.step("API Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("API Test Failed: {}", result.getMethod().getMethodName());
        Allure.step("API Test Failed: " + result.getMethod().getMethodName());

        addApiRequestDetails();
        addFailureDetails(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("API Test Skipped: {} - Reason: {}",
            result.getMethod().getMethodName(),
            result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");
        Allure.step("API Test Skipped: " + result.getMethod().getMethodName());
    }

    private void addApiRequestDetails() {
        try {
            ApiResponse response = ApiManager.getLastResponse();
            if (response != null) {
                Allure.addAttachment("Request URL", response.url() != null ? response.url() : "N/A");
                Allure.addAttachment("Request Method", response.method() != null ? response.method() : "N/A");
                Allure.addAttachment("Status Code", String.valueOf(response.statusCode()));
                Allure.addAttachment("Response Headers", response.headers().toString());
                Allure.addAttachment("Response Body", response.prettyPrint());
                logger.info("API Request/Response details added to Allure report");
            }
        } catch (Exception e) {
            logger.error("Failed to add API request details", e);
        }
    }

    private void addFailureDetails(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Allure.addAttachment("Error Message", throwable.getMessage());
            Allure.addAttachment("Stack Trace", throwable.toString());
            logger.error("API Test Failure Details: {}", throwable.getMessage());
        }
    }
}
