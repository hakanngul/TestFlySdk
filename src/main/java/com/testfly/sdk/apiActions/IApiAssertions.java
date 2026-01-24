package com.testfly.sdk.apiActions;

import com.testfly.sdk.api.ApiResponse;
import com.testfly.sdk.exceptions.ValidationException;
import io.qameta.allure.Step;
import java.util.Arrays;
import java.util.List;

public interface IApiAssertions extends IApiContext {

    ApiResponse getResponse();

    // =================================================
    // 1. STATUS CODE ASSERTIONS
    // =================================================

    @Step("Status code should be {expected}")
    default void assertStatusCodeEquals(int expected) {
        ApiResponse response = getResponse();
        if (response.statusCode() != expected) {
            throw new ValidationException(String.format("[%s %s] Status Code Mismatch! Expected: %d, Actual: %d",
                    response.method(), response.url(), expected, response.statusCode()));
        }
    }

    @Step("Request should be successful (2xx)")
    default void assertRequestIsSuccessful() {
        if (!getResponse().isSuccessful()) {
            throw new ValidationException("Request failed! Status: " + getResponse().statusCode());
        }
    }

    @Step("Status code should be one of {expectedCodes}")
    default void assertStatusCodeIsOneOf(int... expectedCodes) {
        int actual = getResponse().statusCode();
        boolean match = Arrays.stream(expectedCodes).anyMatch(code -> code == actual);
        if (!match) {
            throw new ValidationException("Status code is not in the expected list: " + actual);
        }
    }

    // =================================================
    // 2. HEADER ASSERTIONS
    // =================================================

    @Step("Header '{headerName}' should equal '{expectedValue}'")
    default void assertHeaderEquals(String headerName, String expectedValue) {
        String actual = getResponse().getHeader(headerName).orElse(null);
        if (actual == null || !actual.equals(expectedValue)) {
            throw new ValidationException(String.format("Header Mismatch! [%s] Expected: %s, Actual: %s",
                    headerName, expectedValue, actual));
        }
    }

    @Step("Content-Type should contain '{expectedType}'")
    default void assertContentTypeContains(String expectedType) {
        String actual = getResponse().getHeader("Content-Type").orElse("");
        if (!actual.contains(expectedType)) {
            throw new ValidationException("Content-Type Mismatch! Expected to contain: " + expectedType + ", Actual: " + actual);
        }
    }

    // =================================================
    // 3. JSON DATA ASSERTIONS (Equality)
    // =================================================

    @Step("JSON path '{path}' should equal '{expectedValue}'")
    default void assertJsonPathEquals(String path, Object expectedValue) {
        Object actualValue = getResponse().jsonPath(path);
        // String.valueOf kullanarak tip güvenliği sağlıyoruz (Int vs String hatası almamak için)
        if (actualValue == null || !String.valueOf(actualValue).equals(String.valueOf(expectedValue))) {
            throw new ValidationException(String.format("Data Mismatch! Path: %s\nExpected: %s\nActual:   %s",
                    path, expectedValue, actualValue));
        }
    }

    @Step("Response body should contain '{text}'")
    default void assertBodyContains(String text) {
        if (!getResponse().body().contains(text)) {
            throw new ValidationException("Body text missing! Expected to contain: " + text);
        }
    }

    // =================================================
    // 4. NEGATIVE ASSERTIONS (Not...)
    // =================================================

    @Step("Response body should NOT contain '{text}'")
    default void assertBodyDoesNotContain(String text) {
        if (getResponse().body().contains(text)) {
            throw new ValidationException("Security/Data Error! Body contains restricted text: " + text);
        }
    }

    @Step("JSON path '{path}' should be NULL or Missing")
    default void assertJsonPathIsNull(String path) {
        Object value = getResponse().jsonPath(path);
        if (value != null) {
            throw new ValidationException(String.format("Expected null/missing but found data! Path: %s, Value: %s", path, value));
        }
    }

    @Step("JSON path '{path}' should NOT be null")
    default void assertJsonPathIsNotNull(String path) {
        Object value = getResponse().jsonPath(path);
        if (value == null) {
            throw new ValidationException("Value is null! Path: " + path);
        }
    }

    // =================================================
    // 5. NUMERIC & LOGIC ASSERTIONS
    // =================================================

    @Step("JSON path '{path}' should be greater than {minValue}")
    default void assertJsonPathGreaterThan(String path, double minValue) {
        Object val = getResponse().jsonPath(path);
        double actual = Double.parseDouble(String.valueOf(val));
        if (actual <= minValue) {
            throw new ValidationException(String.format("Value too low! Path: %s, Limit: >%f, Actual: %f", path, minValue, actual));
        }
    }

    @Step("JSON path '{path}' should be less than {maxValue}")
    default void assertJsonPathLessThan(String path, double maxValue) {
        Object val = getResponse().jsonPath(path);
        double actual = Double.parseDouble(String.valueOf(val));
        if (actual >= maxValue) {
            throw new ValidationException(String.format("Value too high! Path: %s, Limit: <%f, Actual: %f", path, maxValue, actual));
        }
    }

    // =================================================
    // 6. COLLECTION & INTEGRITY ASSERTIONS
    // =================================================

    @Step("List at '{path}' should NOT be empty")
    default void assertListIsNotEmpty(String path) {
        List<?> list = getResponse().jsonPath(path);
        if (list == null || list.isEmpty()) {
            throw new ValidationException("List is empty or null! Path: " + path);
        }
    }

    @Step("List at '{path}' size should be {size}")
    default void assertListSizeEquals(String path, int size) {
        List<?> list = getResponse().jsonPath(path);
        if (list == null || list.size() != size) {
            throw new ValidationException(String.format("List size mismatch! Path: %s, Expected: %d, Actual: %d",
                    path, size, (list == null ? 0 : list.size())));
        }
    }

    @Step("JSON path '{path}' should NOT be null or empty")
    default void assertJsonPathIsNotEmpty(String path) {
        Object value = getResponse().jsonPath(path);
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            throw new ValidationException("Value is null or empty! Path: " + path);
        }
    }

    // =================================================
    // 7. PERFORMANCE ASSERTIONS
    // =================================================

    @Step("Response time should be less than {maxMs}ms")
    default void assertResponseTimeLessThan(long maxMs) {
        long time = getResponse().responseTime();
        if (time > maxMs) {
            throw new ValidationException(String.format("Slow Response! Limit: %dms, Actual: %dms", maxMs, time));
        }
    }
}