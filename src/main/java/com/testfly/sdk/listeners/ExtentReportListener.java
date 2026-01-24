package com.testfly.sdk.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.microsoft.playwright.Page;
import com.testfly.sdk.manager.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(ExtentReportListener.class);
    private static final String OUTPUT_FOLDER = "target/extent-reports/";
    private static final String FILE_NAME = "extent-report.html";

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        setupExtentReport();
        logger.info("Extent Report started");
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
        logger.info("Extent Report finished");
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
        extentTest.assignCategory(result.getMethod().getTestClass().getName());
        extentTest.info("Test Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        getTest().log(Status.PASS, "Test Passed");
        getTest().pass("Test passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        getTest().log(Status.FAIL, "Test Failed");
        getTest().fail(result.getThrowable());

        String screenshotPath = captureScreenshot(result);
        if (screenshotPath != null) {
            getTest().addScreenCaptureFromPath(screenshotPath);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        getTest().log(Status.SKIP, "Test Skipped");
        getTest().skip(result.getThrowable());
    }

    private void setupExtentReport() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportPath = OUTPUT_FOLDER + FILE_NAME;

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("TestFly Automation Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimelineEnabled(true);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
    }

    private ExtentTest getTest() {
        return test.get();
    }

    private String captureScreenshot(ITestResult result) {
        try {
            Page page = DriverManager.getPage();
            if (page != null) {
                String screenshotPath = OUTPUT_FOLDER + "screenshots/" +
                    result.getMethod().getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";

                java.nio.file.Path path = Paths.get(screenshotPath);
                java.nio.file.Files.createDirectories(path.getParent());
                page.screenshot(new Page.ScreenshotOptions().setPath(path));

                logger.info("Screenshot captured for ExtentReport: {}", screenshotPath);
                return screenshotPath;
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for ExtentReport", e);
        }
        return null;
    }
}
