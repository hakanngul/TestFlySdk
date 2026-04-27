package com.testfly.sdk.api.actions;

import com.testfly.sdk.api.engine.ApiResponse;
import io.qameta.allure.Step;
import java.util.Map;

public interface ApiActions extends ApiContext {

    @Step("API GET -> {endpoint}")
    default ApiResponse get(String endpoint) {
        return execute("GET", endpoint, null, null);
    }

    @Step("API GET -> {endpoint} | Headers: {headers}")
    default ApiResponse get(String endpoint, Map<String, String> headers) {
        return execute("GET", endpoint, headers, null);
    }

    @Step("API POST -> {endpoint}")
    default ApiResponse post(String endpoint, Object body) {
        return execute("POST", endpoint, null, body);
    }

    @Step("API POST -> {endpoint} | Headers: {headers}")
    default ApiResponse post(String endpoint, Object body, Map<String, String> headers) {
        return execute("POST", endpoint, headers, body);
    }

    @Step("API PUT -> {endpoint}")
    default ApiResponse put(String endpoint, Object body) {
        return execute("PUT", endpoint, null, body);
    }

    @Step("API PATCH -> {endpoint}")
    default ApiResponse patch(String endpoint, Object body) {
        return execute("PATCH", endpoint, null, body);
    }

    @Step("API DELETE -> {endpoint}")
    default ApiResponse delete(String endpoint) {
        return execute("DELETE", endpoint, null, null);
    }

    ApiResponse execute(String method, String endpoint, Map<String, String> headers, Object body);
}
