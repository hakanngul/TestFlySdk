package com.testfly.sdk.tests.web;

import com.testfly.sdk.web.base.BaseWebTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class WebSteps extends BaseWebTest {

    private final LoginPage loginPage = new LoginPage();

    @Given("I navigate to the login page {string}")
    public void iNavigateToTheLoginPage(String url) {
        logger.info("Navigating to: {}", url);
        navigateTo(url);

        assertThat(loginPage.getLoginButton()).isVisible();
    }

    @When("I enter the username {string}")
    public void iEnterTheUsername(String username) {
        loginPage.enterUsername(username);

        assertThat(loginPage.getUsernameInput()).hasValue(username);
    }

    @And("I enter the password {string}")
    public void iEnterThePassword(String password) {
        loginPage.enterPassword(password);

        assertThat(loginPage.getPasswordInput()).hasValue(password);
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLogin();
    }

    @Then("I should be redirected to the inventory page")
    public void iShouldBeRedirectedToTheInventoryPage() {
        assertThat(getPage()).hasURL(java.util.regex.Pattern.compile(".*inventory\\.html"));
    }

    @And("the page title should be {string}")
    public void thePageTitleShouldBe(String expectedTitle) {
        assertThat(loginPage.getInventoryTitle()).hasText(expectedTitle);
    }

    @Then("I should see the error message {string}")
    public void iShouldSeeTheErrorMessage(String expectedMessage) {
        assertThat(loginPage.getErrorMessageElement()).isVisible();
        assertThat(loginPage.getErrorMessageElement()).containsText(expectedMessage);

        logger.warn("Error message verification completed for: {}", expectedMessage);
    }
}
