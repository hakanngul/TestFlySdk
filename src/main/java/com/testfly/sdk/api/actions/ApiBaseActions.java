package com.testfly.sdk.api.actions;

import com.testfly.sdk.api.engine.ApiResponse;
import java.util.Map;

public interface ApiBaseActions extends ApiActions, ApiAssertions {

    void setQueryParam(String key, Object value);
    void setHeader(String key, String value);
    void setBodyData(String key, Object value);
    void clearRequestData();

    default void setBasicAuth(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = java.util.Base64.getEncoder().encodeToString(credentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        setHeader("Authorization", "Basic " + encoded);
    }

    default void setBearerToken(String token) {
        setHeader("Authorization", "Bearer " + token);
    }

    default void setApiKey(String key, String value) {
        setHeader(key, value);
    }

    default void setAuthToken(String scheme, String token) {
        setHeader("Authorization", scheme + " " + token);
    }

    ApiResponse execute(String method, String endpoint, Map<String, String> headers, Object body);
}
