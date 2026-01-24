package com.testfly.sdk.listeners;

import com.testfly.sdk.manager.DriverManager;
import com.testfly.sdk.config.ConfigManager;
import com.testfly.sdk.context.ScenarioContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class ExecutionListener implements IInvokedMethodListener {

    private static final Logger logger = LogManager.getLogger(ExecutionListener.class);

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            // isCucumberTest kontrolünü burada da yapabilirsin
            if (!isCucumberTest()) {
                String browser = System.getProperty("browser", ConfigManager.get().browser());
                logger.info("🚀 Listener: Initializing driver for test: " + method.getTestMethod().getMethodName());
                DriverManager.initializeDriver(browser);
            }
            ScenarioContext.clear();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            if (!isCucumberTest()) {
                logger.info("🛑 Listener: Closing driver for test: " + method.getTestMethod().getMethodName());
                DriverManager.quitDriver();
            }
            ScenarioContext.clear();
        }
    }

    private boolean isCucumberTest() {
        // Senin mevcut stacktrace kontrolün buraya gelecek
        return false;
    }
}