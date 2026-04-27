package com.testfly.sdk.core;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.testfly.sdk.api.engine.ApiResponse;
import com.testfly.sdk.exceptions.FrameworkException;

import java.util.HashMap;
import java.util.Map;

public class ApiManager {

    private static final ThreadLocal<APIRequest> apiRequestThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<APIRequestContext> apiContextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> defaultHeadersThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<ApiResponse> lastResponse = new ThreadLocal<>();

    private ApiManager() {
    }

    public static void initializeApiContext() {
        initializeApiContext(new HashMap<>());
    }

    public static void initializeApiContext(Map<String, String> headers) {
        Playwright playwright = PlaywrightManager.getOrCreate();

        APIRequest apiRequest = playwright.request();
        apiRequestThreadLocal.set(apiRequest);

        Map<String, String> combinedHeaders = new HashMap<>();
        combinedHeaders.put("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        combinedHeaders.put("Accept", "*/*");
        if (headers != null) {
            combinedHeaders.putAll(headers);
        }
        defaultHeadersThreadLocal.set(combinedHeaders);

        APIRequestContext apiContext = apiRequest.newContext(
                new APIRequest.NewContextOptions()
                        .setIgnoreHTTPSErrors(true)
                        .setTimeout(ConfigManager.get().apiReadTimeout())
                        .setExtraHTTPHeaders(combinedHeaders));
        apiContextThreadLocal.set(apiContext);
    }

    public static APIRequestContext getApiContext() {
        APIRequestContext context = apiContextThreadLocal.get();
        if (context == null) {
            throw new FrameworkException("API Context not initialized. Call initializeApiContext() first.");
        }
        return context;
    }

    public static void setDefaultHeaders(Map<String, String> headers) {
        defaultHeadersThreadLocal.set(headers);
    }

    public static Map<String, String> getDefaultHeaders() {
        return defaultHeadersThreadLocal.get();
    }

    public static void disposeContext() {
        APIRequestContext apiContext = apiContextThreadLocal.get();
        if (apiContext != null) {
            apiContext.dispose();
            apiContextThreadLocal.remove();
        }

        apiRequestThreadLocal.remove();
        defaultHeadersThreadLocal.remove();
    }

    public static void clearAll() {
        apiRequestThreadLocal.remove();
        apiContextThreadLocal.remove();
        defaultHeadersThreadLocal.remove();
        lastResponse.remove();
    }

    public static void setLastResponse(ApiResponse response) {
        lastResponse.set(response);
    }

    public static ApiResponse getLastResponse() {
        return lastResponse.get();
    }
}
