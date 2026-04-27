package com.testfly.sdk.api.base;

import com.testfly.sdk.api.engine.ApiEngine;
import com.testfly.sdk.api.engine.ApiResponse;
import com.testfly.sdk.api.actions.ApiBaseActions;
import com.testfly.sdk.core.ConfigManager;
import com.testfly.sdk.core.ApiManager;
import com.testfly.sdk.exceptions.FrameworkException;
import com.microsoft.playwright.APIRequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseApiTest implements ApiBaseActions {

    protected final Logger logger;
    protected String baseUrl;

    private static final ThreadLocal<Map<String, Object>> queryParams = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, String>> storedHeaders = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Object>> bodyData = ThreadLocal.withInitial(HashMap::new);

    public BaseApiTest() {
        this.logger = LogManager.getLogger(this.getClass());
        this.baseUrl = ConfigManager.get().baseUrl();
    }

    protected void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected Map<String, Object> getBodyData() {
        return bodyData.get();
    }

    @Override
    public ApiResponse getResponse() {
        ApiResponse response = ApiManager.getLastResponse();
        if (response == null) {
            throw new FrameworkException("Error: No API response found in memory. Make a request first!");
        }
        return response;
    }

    @Override
    public void setQueryParam(String key, Object value) { queryParams.get().put(key, value); }

    @Override
    public void setHeader(String key, String value) { storedHeaders.get().put(key, value); }

    @Override
    public void setBodyData(String key, Object value) { bodyData.get().put(key, value); }

    @Override
    public void clearRequestData() {
        queryParams.get().clear();
        storedHeaders.get().clear();
        bodyData.get().clear();
    }

    @Override public APIRequestContext getRequestContext() { return ApiManager.getApiContext(); }
    @Override public Logger getLogger() { return this.logger; }
    @Override public String getBaseUrl() { return this.baseUrl; }

    @Override
    public ApiResponse execute(String method, String endpoint, Map<String, String> manualHeaders, Object manualBody) {
        try {
            Map<String, String> finalHeaders = new HashMap<>(this.storedHeaders.get());
            if (manualHeaders != null) finalHeaders.putAll(manualHeaders);

            Object finalBody = (manualBody != null) ? manualBody : (this.bodyData.get().isEmpty() ? null : this.bodyData.get());

            ApiResponse response = new ApiEngine(this).executeWithRetry(
                    method, endpoint, finalHeaders, finalBody, this.queryParams.get()
            );

            ApiManager.setLastResponse(response);

            return response;
        } finally {
            clearRequestData();
            clearStaticRequestData();
        }
    }

    public static void clearStaticRequestData() {
        queryParams.get().clear();
        storedHeaders.get().clear();
        bodyData.get().clear();
    }
}
