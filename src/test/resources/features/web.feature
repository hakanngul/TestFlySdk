@web @ui @login
Feature: SauceDemo Login Functionality
  As a user, I want to access the inventory page securely.

  Background:
    Given I navigate to the login page "https://www.saucedemo.com/"

  # --- HAPPY PATH ---
  @smoke
  Scenario: Successful login with standard user
    When I enter the username "standard_user"
    And I enter the password "secret_sauceXXX"
    And I click the login button
    Then I should be redirected to the inventory page
    And the page title should be "Products"

  # --- NEGATIVE PATH (Data Driven) ---
  @regression
  Scenario Outline: Login failure with invalid credentials
    When I enter the username "<username>"
    And I enter the password "<password>"
    And I click the login button
    Then I should see the error message "<error_message>"

    Examples:
      | username        | password     | error_message                                                             |
      | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out.                       |
      | standard_user   | wrong_pass   | Epic sadface: Username and password do not match any user in this service |
      |                 | secret_sauce | Epic sadface: Username is required                                        |

  # --- VIDEO RECORDING TEST (Intentional Failure) ---
  @regression
  Scenario: Intentional failure - wrong page title to test video recording
    When I enter the username "standard_user"
    And I enter the password "secret_sauce"
    And I click the login button
    Then the page title should be "WRONG TITLE - This will fail on purpose"
