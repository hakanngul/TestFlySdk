package com.testfly.sdk.bdd.hooks;

import com.testfly.sdk.core.ApiManager;
import com.testfly.sdk.core.PlaywrightManager;
import com.testfly.sdk.api.base.BaseApiTest;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApiHooks {

    private static final Logger logger = LogManager.getLogger(ApiHooks.class);

    @Before(value = "@api", order = 1)
    public void beforeApiScenario(Scenario scenario) {
        logger.info("API Scenario Started: {}", scenario.getName());
        ApiManager.initializeApiContext();
    }

    @After(value = "@api", order = 1)
    public void afterApiScenario(Scenario scenario) {
        BaseApiTest.clearStaticRequestData();

        logger.info("Static request data cleared for thread: {}", Thread.currentThread().threadId());
    }

    @After(value = "@api", order = 2)
    public void cleanupApi() {
        ApiManager.disposeContext();
        logger.info("API Context disposed for thread: {}", Thread.currentThread().threadId());
    }

    @AfterAll
    public static void afterAll() {
        PlaywrightManager.dispose();
    }
}
