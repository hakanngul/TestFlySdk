package com.testfly.sdk.tests.api;

import com.testfly.sdk.api.base.BaseApiTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ApiSteps extends BaseApiTest {

    @Given("I set the base URL to {string}")
    public void setBaseUrlStep(String url) {
        setBaseUrl(url);
    }

    @When("I set request body {string} to {string}")
    public void setBodyString(String key, String value) {
        setBodyData(key, value);
    }

    @When("I set request body {string} to {int}")
    public void setBodyInt(String key, int value) {
        setBodyData(key, value);
    }

    @When("I send a GET request to {string}")
    public void sendGet(String endpoint) { get(endpoint); }

    @When("I send a POST request to {string}")
    public void sendPost(String endpoint) { post(endpoint, null); }

    @When("I send a PUT request to {string}")
    public void sendPut(String endpoint) { put(endpoint, null); }

    @When("I send a PATCH request to {string}")
    public void sendPatch(String endpoint) { patch(endpoint, null); }

    @When("I send a DELETE request to {string}")
    public void sendDelete(String endpoint) { delete(endpoint); }

    @Then("the response status code should be {int}")
    public void checkStatusCode(int status) {
        assertStatusCodeEquals(status);
    }

    @And("the response time should be less than {long} ms")
    public void checkResponseTime(long time) {
        assertResponseTimeLessThan(time);
    }

    @And("the header {string} should contain {string}")
    public void checkHeaderContains(String headerName, String value) {
        if (headerName.equalsIgnoreCase("Content-Type")) {
            assertContentTypeContains(value);
        } else {
            assertHeaderEquals(headerName, value);
        }
    }

    @And("the list at {string} should not be empty")
    public void checkListNotEmpty(String path) {
        assertListIsNotEmpty(path);
    }

    @And("the list at {string} size should be {int}")
    public void checkListSize(String path, int size) {
        assertListSizeEquals(path, size);
    }

    @And("the JSON path {string} should equal {string}")
    public void checkJsonPathString(String path, String value) {
        assertJsonPathEquals(path, value);
    }

    @And("the JSON path {string} should be greater than {int}")
    public void checkJsonPathGreater(String path, int value) {
        assertJsonPathGreaterThan(path, value);
    }

    @And("the JSON path {string} should not be null")
    public void checkJsonPathNotNull(String path) {
        assertJsonPathIsNotEmpty(path);
    }

    @And("the response body should not contain {string}")
    public void checkBodyNotContain(String text) {
        assertBodyDoesNotContain(text);
    }

    @And("the response body should equal {string}")
    public void checkBodyExact(String text) {
        if (!getResponse().body().trim().equals(text)) {
            throw new RuntimeException("Body exact match failed! Expected: " + text);
        }
    }
}
