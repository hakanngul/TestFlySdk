package com.testfly.sdk.reporting.listeners;

import com.microsoft.playwright.Page;
import com.testfly.sdk.core.ConfigManager;
import com.testfly.sdk.core.BrowserManager;
import com.testfly.sdk.core.PlaywrightManager;
import com.testfly.sdk.context.ScenarioContext;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.Arrays;

public class WebTestListener implements ITestListener, IInvokedMethodListener {

    private static final Logger logger = LogManager.getLogger(WebTestListener.class);
    private static final ThreadLocal<Boolean> isCucumberTest = ThreadLocal.withInitial(() -> false);

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            boolean isCucumber = isCucumberTest(testResult);

            logger.info(">>> Method Invocation Start: {} | Is Cucumber: {}",
                    method.getTestMethod().getMethodName(), isCucumber);

            if (!isCucumber) {
                String browser = System.getProperty("browser", ConfigManager.get().browser());
                logger.info("Initializing browser via Listener for: " + browser);
                BrowserManager.initializeDriver(browser);
            }

            ScenarioContext.clear();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            boolean isCucumber = isCucumberTest(testResult);

            if (!isCucumber) {
                logger.info("Closing browser via Listener for test: " + testResult.getName());
                BrowserManager.quitDriver();
            }

            ScenarioContext.clear();
        }
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("=== Web Test Suite Started: {} ===", context.getName());

        String[] classNames = Arrays.stream(context.getAllTestMethods())
            .map(m -> m.getTestClass().getName())
            .distinct()
            .toArray(String[]::new);

        for (String className : classNames) {
            if (className.contains("CucumberTests") || className.contains("runners")) {
                isCucumberTest.set(true);
                logger.info("Cucumber test detected - disabling WebTestListener screenshot");
                break;
            }
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== Web Test Suite Finished: {} ===", context.getName());
        PlaywrightManager.dispose();
        isCucumberTest.remove();
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Web Test Started: {}", result.getMethod().getMethodName());
        Allure.step("Web Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Web Test Passed: {}", result.getMethod().getMethodName());
        Allure.step("Web Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.info("Web Test Failed: {}, Is Cucumber: {}",
            result.getMethod().getMethodName(),
            isCucumberTest.get());

        if (!isCucumberTest.get()) {
            logger.error("Web Test Failed: {}", result.getMethod().getMethodName());
            Allure.step("Web Test Failed: " + result.getMethod().getMethodName());

            takeScreenshot(result);
            addFailureDetails(result);
        } else {
            logger.info("Cucumber test - screenshot handled by Cucumber hooks");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Web Test Skipped: {} - Reason: {}",
            result.getMethod().getMethodName(),
            result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");
        Allure.step("Web Test Skipped: " + result.getMethod().getMethodName());
    }

    private boolean isCucumberTest(ITestResult result) {
        String className = result.getTestClass().getName();
        if (className.contains("CucumberTests") || className.contains("runners")) {
            return true;
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains("cucumber.runtime") ||
                    element.getClassName().contains("io.cucumber")) {
                return true;
            }
        }
        return false;
    }

    private void takeScreenshot(ITestResult result) {
        try {
            Page page = BrowserManager.getPage();
            if (page != null) {
                byte[] screenshot = page.screenshot();
                java.nio.file.Files.createDirectories(Paths.get("target/screenshots"));
                String screenshotPath = "target/screenshots/" +
                    result.getMethod().getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
                java.nio.file.Files.write(Paths.get(screenshotPath), screenshot);

                Allure.addAttachment(
                    "Web_Screenshot_" + result.getMethod().getMethodName() + ".png",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    "png"
                );
                logger.info("Screenshot taken for failed test: {}", result.getMethod().getMethodName());
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
        }
    }

    private void addFailureDetails(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Allure.addAttachment("Web Stack Trace", throwable.getMessage());
            logger.error("Web Test Failure Details: {}", throwable.getMessage());
        }
    }
}
