package com.testfly.sdk.base;

import com.testfly.sdk.api.ApiEngine;
import com.testfly.sdk.api.ApiResponse;
import com.testfly.sdk.apiActions.IApiBaseActions;
import com.testfly.sdk.config.ConfigManager;
import com.testfly.sdk.exceptions.FrameworkException;
import com.testfly.sdk.manager.ApiContextManager;
import com.microsoft.playwright.APIRequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseApi implements IApiBaseActions {

    protected final Logger logger;
    protected String baseUrl;

    private static final ThreadLocal<Map<String, Object>> queryParams = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, String>> storedHeaders = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Object>> bodyData = ThreadLocal.withInitial(HashMap::new);

    public BaseApi() {
        this.logger = LogManager.getLogger(this.getClass());
        this.baseUrl = ConfigManager.get().baseUrl();
    }

    protected void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected Map<String, Object> getBodyData() {
        return bodyData.get();
    }

    // --- Preparation Methods from Interface ---

    @Override
    public ApiResponse getResponse() {
        ApiResponse response = ApiContextManager.getLastResponse();
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

    // --- Core Implementations ---
    @Override public APIRequestContext getRequestContext() { return ApiContextManager.getApiContext(); }
    @Override public Logger getLogger() { return this.logger; }
    @Override public String getBaseUrl() { return this.baseUrl; }

    @Override
    public ApiResponse execute(String method, String endpoint, Map<String, String> manualHeaders, Object manualBody) {
        try {
            // Combine preparation and manual data
            Map<String, String> finalHeaders = new HashMap<>(this.storedHeaders.get());
            if (manualHeaders != null) finalHeaders.putAll(manualHeaders);

            Object finalBody = (manualBody != null) ? manualBody : (this.bodyData.get().isEmpty() ? null : this.bodyData.get());

            ApiResponse response = new ApiEngine(this).executeWithRetry(
                    method, endpoint, finalHeaders, finalBody, this.queryParams.get()
            );

            ApiContextManager.setLastResponse(response);

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