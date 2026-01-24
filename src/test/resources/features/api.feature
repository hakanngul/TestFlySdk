@api
Feature: JSONPlaceholder CRUD Operations Demo

  Background:
    Given I set the base URL to "https://jsonplaceholder.typicode.com"

  # --- GET (READ) SCENARIOS ---

  Scenario: Get all posts and verify list structure
    When I send a GET request to "/posts"
    Then the response status code should be 200
    And the response time should be less than 2000 ms
    And the header "Content-Type" should contain "application/json"
    And the list at "$" should not be empty
    And the list at "$" size should be 100

  Scenario: Get single user and verify details
    When I send a GET request to "/users/1"
    Then the response status code should be 200
    And the JSON path "$.name" should equal "Leanne Graham"
    And the JSON path "$.address.city" should equal "Gwenborough"
    And the JSON path "$.id" should be greater than 0
    And the response body should not contain "password"

  # --- POST (CREATE) SCENARIO ---

  Scenario: Create a new post successfully
    When I set request body "title" to "TestFly SDK Demo"
    And I set request body "body" to "Otomasyon harika gidiyor"
    And I set request body "userId" to 1
    And I send a POST request to "/posts"
    Then the response status code should be 201
    And the JSON path "$.title" should equal "TestFly SDK Demo"
    And the JSON path "$.id" should not be null

  # --- PUT (FULL UPDATE) SCENARIO ---

  Scenario: Update an existing post entirely
    When I set request body "id" to 1
    And I set request body "title" to "Updated Title via PUT"
    And I set request body "body" to "New Body Content"
    And I set request body "userId" to 1
    And I send a PUT request to "/posts/1"
    Then the response status code should be 200
    And the JSON path "$.title" should equal "Updated Title via PUT"

  # --- PATCH (PARTIAL UPDATE) SCENARIO ---

  Scenario: Update only the title of a post
    When I set request body "title" to "Just Patched Title"
    And I send a PATCH request to "/posts/1"
    Then the response status code should be 200
    And the JSON path "$.title" should equal "Just Patched Title"
    And the JSON path "$.userId" should not be null

  # --- DELETE (REMOVE) SCENARIO ---

  Scenario: Delete a post
    When I send a DELETE request to "/posts/1"
    Then the response status code should be 200
    And the response body should equal "{}"