package com.testfly.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.microsoft.playwright.APIResponse;
import com.testfly.sdk.exceptions.FrameworkException;

import java.util.Map;
import java.util.Optional;

public record ApiResponse(
        int statusCode,
        String body,
        Map<String, String> headers,
        long responseTime,
        String url,
        String method
) {

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // --- BASIC STATUS CHECKS ---
    public boolean isSuccessful() { return statusCode >= 200 && statusCode < 300; }
    public boolean isClientError() { return statusCode >= 400 && statusCode < 500; }
    public boolean isServerError() { return statusCode >= 500; }

    // --- JSON QUERYING (JAYWAY POWERED) ---

    /**
     * Retrieves desired data from JSON in a type-safe manner.
     * Example: api.response().<List<String>>jsonPath("$.users[*].name")
     */
    public <T> T jsonPath(String expression) {
        if (!isJson()) {
            throw new FrameworkException(String.format("ERROR: Response is not in JSON format! URL: %s\nBody: %s", url, body));
        }
        try {
            return JsonPath.read(body, expression);
        } catch (PathNotFoundException e) {
            return null; // Return null if value doesn't exist, assertion layer will catch this.
        } catch (Exception e) {
            throw new FrameworkException(String.format("JSON Path Error: [%s] could not be resolved. URL: %s", expression, url), e);
        }
    }

    /**
     * When we want a guaranteed String return
     */
    public String jsonPathString(String expression) {
        Object result = jsonPath(expression);
        return result != null ? String.valueOf(result) : null;
    }

    // --- POJO MAPPING (JACKSON) ---

    public <T> T as(Class<T> type) {
        if (body == null || body.isBlank()) return null;
        try {
            return mapper.readValue(body, type);
        } catch (Exception e) {
            throw new FrameworkException("JSON Mapping Error (" + type.getSimpleName() + ")", e);
        }
    }

    public <T> T as(TypeReference<T> type) {
        if (body == null || body.isBlank()) return null;
        try {
            return mapper.readValue(body, type);
        } catch (Exception e) {
            throw new FrameworkException("JSON Mapping Error (" + type.getType() + ")", e);
        }
    }

    // --- HELPER METHODS ---

    public boolean isJson() {
        return getHeader("Content-Type")
                .map(ct -> ct.toLowerCase().contains("application/json"))
                .orElse(false) && body != null && (body.startsWith("{") || body.startsWith("["));
    }

    public Optional<String> getHeader(String name) {
        // Headers must be searched case-insensitively
        return headers.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public String prettyPrint() {
        if (!isJson()) return body;
        try {
            Object json = mapper.readValue(body, Object.class);
            return mapper.writeValueAsString(json);
        } catch (Exception e) {
            return body;
        }
    }



    // --- FACTORY METHOD ---
    public static ApiResponse create(APIResponse playwrightRes, long time, String methodUsed) {
        // Converting body as byte[] to UTF-8 sometimes resolves character issues
        String bodyText;
        try {
            bodyText = playwrightRes.text();
        } catch (Exception e) {
            bodyText = ""; // Response might be empty or binary
        }

        return new ApiResponse(
                playwrightRes.status(),
                bodyText,
                playwrightRes.headers(),
                time,
                playwrightRes.url(),
                methodUsed
        );
    }


}