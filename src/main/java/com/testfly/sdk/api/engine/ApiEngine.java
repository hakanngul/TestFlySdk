package com.testfly.sdk.api.engine;

import com.google.gson.Gson;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.testfly.sdk.api.base.BaseApiTest;
import com.testfly.sdk.core.ConfigManager;
import com.testfly.sdk.exceptions.ApiRequestException;
import com.testfly.sdk.exceptions.RetryExhaustedException;
import io.qameta.allure.Allure;

import java.util.HashMap;
import java.util.Map;

public class ApiEngine {

    private final BaseApiTest api;
    private final Gson gson = new Gson();

    public ApiEngine(BaseApiTest api) {
        this.api = api;
    }

    public ApiResponse executeWithRetry(String method, String endpoint, Map<String, String> headers, Object body, Map<String, Object> queryParams) {
        int maxRetries = ConfigManager.get().apiMaxRetries();
        long baseDelay = ConfigManager.get().apiRetryDelay();
        int attempts = 0;
        Exception lastException = null;

        while (attempts <= maxRetries) {
            try {
                return run(method, endpoint, headers, body, queryParams);
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts <= maxRetries) {
                    long delay = baseDelay * (1L << (attempts - 1));
                    api.getLogger().warn("Request failed (Attempt {}/{}), retrying in {}ms...", attempts, maxRetries, delay);
                    sleep(delay);
                }
            }
        }
        throw new RetryExhaustedException("Request failed after " + maxRetries + " attempts", attempts, lastException);
    }

    private ApiResponse run(String method, String endpoint, Map<String, String> headers, Object body, Map<String, Object> queryParams) {
        try {
            return doExecute(method, endpoint, headers, body, queryParams);
        } catch (RetryExhaustedException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRequestException(
                String.format("%s %s failed: %s", method, endpoint, e.getMessage()), e);
        }
    }

    private ApiResponse doExecute(String method, String endpoint, Map<String, String> headers, Object body, Map<String, Object> queryParams) {
        long startTime = System.currentTimeMillis();
        String fullUrl = api.getBaseUrl() + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);

        RequestOptions options = RequestOptions.create().setMethod(method);

        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> options.setQueryParam(key, value.toString()));
        }

        Map<String, String> allHeaders = new HashMap<>();
        if (headers != null) allHeaders.putAll(headers);
        allHeaders.forEach(options::setHeader);

        if (body != null) {
            String jsonBody = body instanceof String ? (String) body : gson.toJson(body);
            options.setData(jsonBody);

        if (!allHeaders.containsKey("Content-Type")) {
                options.setHeader("Content-Type", "application/json");
            }
        }

        APIResponse response = api.getRequestContext().fetch(fullUrl, options);
        long responseTime = System.currentTimeMillis() - startTime;

        ApiResponse apiResponse = ApiResponse.create(response, responseTime, method);

        attachToAllure(apiResponse);
        return apiResponse;
    }

    private void attachToAllure(ApiResponse response) {
        Allure.addAttachment("API URL", response.url());
        Allure.addAttachment("Response Status", String.valueOf(response.statusCode()));
        if (response.body() != null) {
            Allure.addAttachment("Response Body", "application/json", response.body());
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
