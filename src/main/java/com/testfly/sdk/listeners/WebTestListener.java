package com.testfly.sdk.listeners;

import com.microsoft.playwright.Page;
import com.testfly.sdk.config.ConfigManager;
import com.testfly.sdk.context.ScenarioContext;
import com.testfly.sdk.manager.DriverManager;
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

    // --- DRIVER MANAGEMENT (IInvokedMethodListener) ---

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            boolean isCucumber = isCucumberTest(testResult);

            logger.info(">>> Method Invocation Start: {} | Is Cucumber: {}",
                    method.getTestMethod().getMethodName(), isCucumber);

            // If not Cucumber (Classic TestNG), we open the driver here
            if (!isCucumber) {
                String browser = System.getProperty("browser", ConfigManager.get().browser());
                logger.info("Initializing driver via Listener for: " + browser);
                DriverManager.initializeDriver(browser);
            }

            // Clear context in all cases
            ScenarioContext.clear();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            boolean isCucumber = isCucumberTest(testResult);

            // If not Cucumber, we close the driver here
            if (!isCucumber) {
                logger.info("Closing driver via Listener for test: " + testResult.getName());
                DriverManager.quitDriver();
            }

            ScenarioContext.clear();
        }
    }

    // --- REPORTING AND SCREENSHOT (ITestListener) ---


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

    // --- HELPER METHODS ---

    private boolean isCucumberTest(ITestResult result) {
        // We ensure this by checking both the class name and the stacktrace
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
            Page page = DriverManager.getPage();
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
