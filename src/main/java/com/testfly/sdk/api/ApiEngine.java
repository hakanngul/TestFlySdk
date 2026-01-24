package com.testfly.sdk.api;

import com.google.gson.Gson;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.testfly.sdk.base.BaseApi;
import com.testfly.sdk.config.ConfigManager;
import com.testfly.sdk.exceptions.FrameworkException;
import io.qameta.allure.Allure;

import java.util.HashMap;
import java.util.Map;

public class ApiEngine {

    private final BaseApi api;
    private final Gson gson = new Gson();

    public ApiEngine(BaseApi api) {
        this.api = api;
    }

    public ApiResponse executeWithRetry(String method, String endpoint, Map<String, String> headers, Object body, Map<String, Object> queryParams) {
        int maxRetries = ConfigManager.get().apiMaxRetries();
        long retryDelay = ConfigManager.get().apiRetryDelay();
        int attempts = 0;
        Exception lastException = null;

        while (attempts <= maxRetries) {
            try {
                return run(method, endpoint, headers, body, queryParams);
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts <= maxRetries) {
                    api.getLogger().warn("Request failed (Attempt {}/{}), retrying in {}ms...", attempts, maxRetries, retryDelay);
                    sleep(retryDelay);
                }
            }
        }
        throw new FrameworkException("Request failed after " + maxRetries + " attempts", lastException);
    }

    private ApiResponse run(String method, String endpoint, Map<String, String> headers, Object body, Map<String, Object> queryParams) {
        long startTime = System.currentTimeMillis();
        String fullUrl = api.getBaseUrl() + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);

        RequestOptions options = RequestOptions.create().setMethod(method);

        // ✅ QUERY PARAMETER HANDLING
        // Lego adımlarından gelen parametreleri Playwright options'a ekliyoruz
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> options.setQueryParam(key, value.toString()));
        }

        // Header Merge & Set
        Map<String, String> allHeaders = new HashMap<>();
        if (headers != null) allHeaders.putAll(headers);
        allHeaders.forEach(options::setHeader);

        // Body Handling
        if (body != null) {
            String jsonBody = body instanceof String ? (String) body : gson.toJson(body);
            options.setData(jsonBody);

        // Content-Type is only added if it hasn't been manually set
        if (!allHeaders.containsKey("Content-Type")) {
                options.setHeader("Content-Type", "application/json");
            }
        }

        // Playwright Fetch
        APIResponse response = api.getRequestContext().fetch(fullUrl, options);
        long responseTime = System.currentTimeMillis() - startTime;

        // Static factory method kullanımı (Önceki düzeltmemiz)
        ApiResponse apiResponse = ApiResponse.create(response, responseTime, method);

        // Logging & Reporting
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