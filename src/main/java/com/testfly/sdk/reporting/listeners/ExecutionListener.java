package com.testfly.sdk.reporting.listeners;

import com.testfly.sdk.core.BrowserManager;
import com.testfly.sdk.core.ConfigManager;
import com.testfly.sdk.core.PlaywrightManager;
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
            if (!isCucumberTest()) {
                String browser = System.getProperty("browser", ConfigManager.get().browser());
                logger.info("Listener: Initializing browser for test: " + method.getTestMethod().getMethodName());
                BrowserManager.initializeDriver(browser);
            }
            ScenarioContext.clear();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            if (!isCucumberTest()) {
                logger.info("Listener: Closing browser for test: " + method.getTestMethod().getMethodName());
                BrowserManager.quitDriver();
            }
            ScenarioContext.clear();
        }
    }

    private boolean isCucumberTest() {
        return false;
    }
}
