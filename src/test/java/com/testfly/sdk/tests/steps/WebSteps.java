package com.testfly.sdk.tests.steps;

import com.testfly.sdk.base.BaseTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * WebSteps, BaseTest'i extend ederek IWebActions ve LogManager yeteneklerini miras alır.
 * Kural: İşlemler Page class'ından çağrılır, doğrulamalar burada assertThat ile yapılır.
 */
public class WebSteps extends BaseTest {

    // Page Object instansı - DriverManager.getPage()'i dahili olarak kullanır.
    private final LoginPage loginPage = new LoginPage();

    // ---------- BACKGROUND ----------

    @Given("I navigate to the login page {string}")
    public void iNavigateToTheLoginPage(String url) {
        logger.info("Navigating to: {}", url);
        navigateTo(url);

        assertThat(loginPage.getLoginButton()).isVisible();
    }

    // ---------- ACTIONS ----------

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

    // ---------- ASSERTIONS ----------

    @Then("I should be redirected to the inventory page")
    public void iShouldBeRedirectedToTheInventoryPage() {
        // Playwright Assertions: URL'in inventory içermesini bekler (Regex kullanımı)
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