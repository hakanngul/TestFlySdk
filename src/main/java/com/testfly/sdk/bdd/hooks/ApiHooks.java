package com.testfly.sdk.bdd.hooks;

import com.testfly.sdk.core.ApiManager;
import com.testfly.sdk.api.base.BaseApiTest;
import com.testfly.sdk.api.engine.ApiResponse;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApiHooks {

    private static final Logger logger = LogManager.getLogger(ApiHooks.class);

    @Before(value = "@api", order = 1)
    public void beforeApiScenario(Scenario scenario) {
        logger.info("API Scenario Started: {}", scenario.getName());
        scenario.log("API scenario started on thread: " + Thread.currentThread().threadId());

        ApiManager.initializeApiContext();
    }

    @After(value = "@api", order = 1)
    public void afterApiScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            logApiFailureDetails(scenario);
        }

        BaseApiTest.clearStaticRequestData();
        logger.info("Static request data cleared for thread: {}", Thread.currentThread().threadId());
    }

    @After(value = "@api", order = 2)
    public void cleanupApi() {
        ApiManager.disposeContext();
        logger.info("API Context disposed for thread: {}", Thread.currentThread().threadId());
    }

    /**
     * Logs API request/response details on scenario failure for debugging.
     */
    private void logApiFailureDetails(Scenario scenario) {
        try {
            ApiResponse response = ApiManager.getLastResponse();
            if (response != null) {
                String failureInfo = String.format(
                        "\n--- API Failure Details ---\nStatus: %s\nBody: %s\n--- End ---",
                        response.statusCode(),
                        truncate(response.body(), 500));
                scenario.log(failureInfo);
                logger.error("API failure details for '{}': Status={}, Body={}",
                        scenario.getName(), response.statusCode(), truncate(response.body(), 200));
            }
        } catch (Exception e) {
            logger.warn("Could not log API failure details: {}", e.getMessage());
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null)
            return "null";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}