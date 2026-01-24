package com.testfly.sdk.hooks;

import com.testfly.sdk.manager.ApiContextManager;
import com.testfly.sdk.base.BaseApi;
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
        // Her thread için izole Playwright APIRequestContext oluşturulur
        ApiContextManager.initializeApiContext();
    }

    @After(value = "@api", order = 1)
    public void afterApiScenario(Scenario scenario) {
        BaseApi.clearStaticRequestData();

        logger.info("Static request data cleared for thread: {}", Thread.currentThread().threadId());
    }

    @After(value = "@api", order = 2)
    public void cleanupApi() {
        // Playwright context'ini kapatıyoruz
        ApiContextManager.disposeContext();
        logger.info("API Context disposed for thread: {}", Thread.currentThread().threadId());
    }
}