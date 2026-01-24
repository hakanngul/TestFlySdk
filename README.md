# TestFly SDK

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-3.9+-red)
![Playwright](https://img.shields.io/badge/Playwright-1.48.0-green)
![TestNG](https://img.shields.io/badge/TestNG-7.10.2-blue)
![Cucumber](https://img.shields.io/badge/Cucumber-7.18.1-brightgreen)
![License](https://img.shields.io/badge/License-MIT-yellow)

**A robust, scalable test automation SDK for high-performance UI and API testing**

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Quick Start](#-quick-start)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Writing Tests](#-writing-tests)
- [Running Tests](#-running-tests)
- [Reports](#-reports)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🚀 Overview

TestFly SDK is a modern, high-performance test automation framework built on **Java 21** and **Playwright**. It provides a unified approach for both **UI automation** and **API testing**, supporting both **TestNG** and **Cucumber (BDD)** methodologies.

The framework is designed with a focus on:
- **Speed**: Playwright's WebSocket-based architecture is significantly faster than traditional WebDriver
- **Reliability**: Built-in auto-wait mechanisms reduce flakiness
- **Readability**: Interface-based action composition makes tests read like natural language
- **Parallel Execution**: Thread-safe design enables efficient parallel test execution
- **Type Safety**: Leverages modern Java features (Records, Pattern Matching)

---

## ✨ Key Features

### UI Testing
- **Multi-Browser Support**: Chrome, Firefox, WebKit
- **Headless & Headful Modes**: Flexible execution options
- **Auto-Wait**: Eliminates need for explicit waits
- **Screenshot & Tracing**: Built-in failure debugging
- **Element Actions**: Comprehensive action interfaces (Click, Type, Hover, Drag & Drop, etc.)

### API Testing
- **Unified Layer**: API testing built on Playwright's `APIRequestContext`
- **30-40% Faster**: More efficient than HTTPClient-based libraries
- **Auto-Retry**: Configurable retry logic for flaky endpoints
- **JSON Path**: Query JSON responses using Jayway JsonPath
- **POJO Mapping**: Automatic JSON-to-Object mapping with Jackson

### Architecture
- **Thread-Safe**: Isolated test execution with ThreadLocal
- **Interface Composition**: Modular, composable action interfaces
- **BDD Support**: Native Cucumber integration with Gherkin syntax
- **Reporting**: Allure and ExtentReports support
- **Logging**: Asynchronous Log4j2 for high-performance logging

### Data Handling
- **Multi-Format Support**: JSON, CSV, Excel readers
- **Scenario Context**: Thread-safe data sharing across test steps
- **Type-Safe Configuration**: Owner library for type-safe property management

---

## 🔧 Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Core Language** | Java | 21 |
| **Build Tool** | Maven | 3.9+ |
| **UI Automation** | Playwright | 1.48.0 |
| **Test Runner** | TestNG | 7.10.2 |
| **BDD Framework** | Cucumber | 7.18.1 |
| **API Testing** | Playwright API | 1.48.0 |
| **Reporting** | Allure / ExtentReports | Latest |
| **Logging** | Log4j2 | 2.23.1 |
| **Configuration** | Owner API | 1.0.12 |
| **JSON Processing** | Gson / Jackson | 2.11.0 / 2.17.2 |

---

## 🎯 Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.9 or higher
- Node.js 18+ (for Playwright browser binaries)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/TestFlySdk.git
cd TestFlySdk
```

2. Install Playwright browsers:
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

3. Build the project:
```bash
mvn clean install
```

### Run Your First Test

**TestNG Test:**
```bash
mvn test -Dtest=YourTestClass
```

**Cucumber Test:**
```bash
mvn test -Dcucumber.options="classpath:features/your-feature.feature"
```

---

## 🏗️ Architecture

### Core Components

#### 1. Driver Management (Thread-Safe Parallelism)
The framework uses a **ThreadLocal Strategy** to ensure safe parallel test execution:

```java
// Each thread gets isolated browser instances
DriverManager.initializeDriver("chrome");
Page page = DriverManager.getPage();
// ... perform actions
DriverManager.quitDriver();
```

#### 2. Interface-Based Action Composition
Unlike traditional inheritance-heavy frameworks, TestFly SDK uses **Interface Composition**:

```java
public interface IWebActions extends
    IPageContext,
    INavigationActions,
    IMouseActions,
    IKeyboardActions,
    IElementChecks,
    IWaitActions
```

This allows writing clean, readable test code:
```java
click("#login-button")
type("#username", "testuser")
waitForElement("#dashboard")
```

#### 3. API Engine
API testing built directly on Playwright's `APIRequestContext`:

```java
ApiResponse response = new BaseApi() {}
    .execute("GET", "/users", null, null);

assert response.statusCode() == 200;
assert response.<List<User>>jsonPath("$.users").size() > 0;
```

#### 4. Scenario Context
Thread-safe data repository for sharing state across test steps:

```java
ScenarioContext.set(ContextKey.ACCESS_TOKEN, token);
String token = ScenarioContext.getString(ContextKey.ACCESS_TOKEN);
```

### Design Patterns
- **Wrapper Pattern**: ApiEngine wraps Playwright API calls
- **Factory Pattern**: ApiResponse.create() static factory method
- **Strategy Pattern**: Different browser launch strategies
- **Template Method**: BaseTest and BasePage provide test templates

---

## 📁 Project Structure

```
src/main/java/com/testfly/sdk/
├── actions/              # UI action interfaces
│   ├── IWebActions.java
│   ├── IMouseActions.java
│   ├── IKeyboardActions.java
│   └── ...
├── api/                  # API testing components
│   ├── ApiEngine.java
│   ├── ApiResponse.java
│   └── BaseApi.java
├── apiActions/           # API action interfaces
│   ├── IApiActions.java
│   ├── IApiAssertions.java
│   └── ...
├── base/                 # Base classes
│   ├── BaseTest.java
│   ├── BasePage.java
│   └── BaseApi.java
├── config/               # Configuration
│   ├── ConfigManager.java
│   └── EnvConfig.java
├── context/              # Scenario context
│   └── ScenarioContext.java
├── data/                 # Data readers
│   ├── JsonReader.java
│   ├── CsvReader.java
│   └── ExcelReader.java
├── exceptions/           # Custom exceptions
│   └── FrameworkException.java
├── hooks/                # Cucumber hooks
│   ├── TestHooks.java
│   └── ApiHooks.java
├── listeners/            # TestNG listeners
│   ├── WebTestListener.java
│   ├── ApiTestListener.java
│   └── ...
├── manager/              # Manager classes
│   ├── DriverManager.java
│   ├── ApiContextManager.java
│   └── LogManager.java
└── utils/                # Utility classes
    ├── FileUtils.java
    ├── StringUtils.java
    └── ...

src/test/java/com/testfly/sdk/
├── tests/                # Test implementations
├── steps/                # Cucumber step definitions
└── runners/              # Cucumber test runners

src/test/resources/
├── features/             # Cucumber feature files
├── config.properties     # Configuration file
└── allure.properties     # Allure configuration
```

---

## ⚙️ Configuration

### config.properties

```properties
# Browser Settings
browser=chrome
headless=true
timeout=10000
record.video=false

# Environment
environment=local
base.url=https://localhost:8080

# Log Level
log.level=INFO

# API Settings
api.timeout.connect=30000
api.timeout.read=30000
api.retry.max=3
api.retry.delay=1000
```

### Dynamic Configuration

Use system properties to override settings at runtime:

```bash
mvn test -Dbrowser=firefox -Dheadless=false
```

---

## ✍️ Writing Tests

### TestNG UI Test

```java
public class LoginTest extends BaseTest {

    @Test
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.open()
            .enterUsername("testuser")
            .enterPassword("password123")
            .clickLogin();

        assertTrue(isVisible("#dashboard"));
        assertEquals(getText("#welcome-message"), "Welcome, testuser!");
    }
}
```

### Cucumber BDD Test

#### UI Test Example

**Feature File:**
```gherkin
Feature: User Login

  Scenario: Successful login with valid credentials
    Given I navigate to the login page
    When I enter username "testuser" and password "password123"
    And I click the login button
    Then I should see the dashboard
```

**Step Definitions:**
```java
public class LoginSteps extends BasePage {

    @Given("I navigate to the login page")
    public void navigateToLoginPage() {
        navigateTo("/login");
    }

    @When("I enter username {string} and password {string}")
    public void enterCredentials(String username, String password) {
        type("#username", username);
        type("#password", password);
    }

    @Then("I should see the dashboard")
    public void verifyDashboard() {
        assertTrue(isVisible("#dashboard"));
    }
}
```

#### API Test Example

**Feature File:**
```gherkin
@api
Feature: User Management API

  Scenario: Get all users
    When I send a GET request to "/api/users"
    Then the response status code should be 200
    And the response time should be less than 1000 ms
    And the list at "$.users" should not be empty

  Scenario: Create a new user
    When I set request body "name" to "John Doe"
    And I set request body "email" to "john@example.com"
    And I send a POST request to "/api/users"
    Then the response status code should be 201
    And the JSON path "$.name" should equal "John Doe"
    And the JSON path "$.id" should not be null

  Scenario: Update user with headers
    Given I set base URL to "https://api.example.com"
    When I set request body "name" to "Jane Updated"
    And I send a PUT request to "/api/users/1"
    Then the response status code should be 200
    And the header "Content-Type" should contain "application/json"
```

**Step Definitions:**
```java
public class ApiSteps extends BaseApi {

    // Setup
    @Given("I set the base URL to {string}")
    public void setBaseUrlStep(String url) {
        setBaseUrl(url);
    }

    // Body preparation
    @When("I set request body {string} to {string}")
    public void setBodyString(String key, String value) {
        setBodyData(key, value);
    }

    @When("I send a GET request to {string}")
    public void sendGet(String endpoint) {
        get(endpoint);
    }

    @When("I send a POST request to {string}")
    public void sendPost(String endpoint) {
        post(endpoint, null);
    }

    @When("I send a PUT request to {string}")
    public void sendPut(String endpoint) {
        put(endpoint, null);
    }

    // Assertions
    @Then("the response status code should be {int}")
    public void checkStatusCode(int status) {
        assertStatusCodeEquals(status);
    }

    @And("the response time should be less than {long} ms")
    public void checkResponseTime(long time) {
        assertResponseTimeLessThan(time);
    }

    @And("the list at {string} should not be empty")
    public void checkListNotEmpty(String path) {
        assertListIsNotEmpty(path);
    }

    @And("the JSON path {string} should equal {string}")
    public void checkJsonPathString(String path, String value) {
        assertJsonPathEquals(path, value);
    }

    @And("the JSON path {string} should not be null")
    public void checkJsonPathNotNull(String path) {
        assertJsonPathIsNotNull(path);
    }

    @And("the header {string} should contain {string}")
    public void checkHeaderContains(String headerName, String value) {
        if (headerName.equalsIgnoreCase("Content-Type")) {
            assertContentTypeContains(value);
        } else {
            assertHeaderEquals(headerName, value);
        }
    }
}
```

### API Test

#### Basic GET Request
```java
public class UserApiTest extends BaseApi {

    @Test
    public void testGetUsers() {
        ApiResponse response = get("/api/users");

        assertStatusCodeEquals(200);
        assertRequestIsSuccessful();

        List<User> users = response.<List<User>>jsonPath("$.users");
        assertTrue(users.size() > 0);
    }
}
```

#### POST Request with Body
```java
@Test
public void testCreateUser() {
    User newUser = new User("John Doe", "john@example.com");

    ApiResponse response = post("/api/users", newUser);

    assertStatusCodeEquals(201);
    assertRequestIsSuccessful();

    User createdUser = response.as(User.class);
    assertEquals(createdUser.getEmail(), "john@example.com");
}
```

#### POST Request with Headers
```java
@Test
public void testCreateUserWithAuth() {
    User newUser = new User("Jane Doe", "jane@example.com");

    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer token123");
    headers.put("Content-Type", "application/json");

    ApiResponse response = post("/api/users", newUser, headers);

    assertStatusCodeEquals(201);
    assertHeaderEquals("Content-Type", "application/json");
}
```

#### PUT Request
```java
@Test
public void testUpdateUser() {
    User updatedUser = new User("John Updated", "john.updated@example.com");

    ApiResponse response = put("/api/users/1", updatedUser);

    assertStatusCodeEquals(200);
    assertJsonPathEquals("$.email", "john.updated@example.com");
}
```

#### DELETE Request
```java
@Test
public void testDeleteUser() {
    ApiResponse response = delete("/api/users/1");

    assertStatusCodeEquals(204);
    assertRequestIsSuccessful();
}
```

#### Request with Query Parameters
```java
@Test
public void testGetUsersWithPagination() {
    setQueryParam("page", "1");
    setQueryParam("limit", "10");

    ApiResponse response = get("/api/users");

    assertStatusCodeEquals(200);
    assertResponseTimeLessThan(1000);
}
```

#### Complex Assertions
```java
@Test
public void testUserCreationWithAssertions() {
    User newUser = new User("Test User", "test@example.com");

    ApiResponse response = post("/api/users", newUser);

    // Status code assertion
    assertStatusCodeEquals(201);

    // Success assertion
    assertRequestIsSuccessful();

    // Header assertions
    assertHeaderEquals("Content-Type", "application/json");

    // JSON path assertions
    assertJsonPathEquals("$.name", "Test User");
    assertJsonPathEquals("$.email", "test@example.com");

    // List assertions
    List<User> allUsers = get("/api/users").<List<User>>jsonPath("$.users");
    assertListSizeEquals("$.users", allUsers.size());

    // Negative assertions
    assertBodyDoesNotContain("password");

    // Performance assertion
    assertResponseTimeLessThan(2000);
}
```

#### Using Response Directly
```java
@Test
public void testGetUserById() {
    ApiResponse response = get("/api/users/1");

    // Check response status
    assertEquals(response.statusCode(), 200);
    assertTrue(response.isSuccessful());

    // Get specific field
    String name = response.<String>jsonPath("$.name");
    assertEquals(name, "John Doe");

    // Convert to POJO
    User user = response.as(User.class);
    assertNotNull(user);

    // Get header
    String contentType = response.getHeader("Content-Type").orElse("");
    assertTrue(contentType.contains("application/json"));

    // Pretty print
    logger.info("Response:\n{}", response.prettyPrint());
}
```

---

## 🚦 Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=LoginTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=LoginTest#testSuccessfulLogin
```

### Run with Specific Browser
```bash
mvn test -Dbrowser=firefox
```

### Run in Headful Mode
```bash
mvn test -Dheadless=false
```

### Run Cucumber Tests
```bash
mvn test -Dcucumber.options="--tags @ui"
```

### Parallel Execution
The framework supports parallel execution out of the box (configured in `pom.xml`):
```xml
<parallel>methods</parallel>
<threadCount>4</threadCount>
```

---

## 📊 Reports

### Allure Report

Generate and view Allure reports:
```bash
mvn test
mvn allure:serve
```

### ExtentReports

ExtentReports are automatically generated in `target/extent-reports/` after test execution.

### Screenshots

Screenshots are automatically captured on test failures and saved to `target/screenshots/`.

---

## 📚 Additional Resources

- [Architecture Documentation](Artitecture.md) - Detailed architecture analysis
- [Playwright Documentation](https://playwright.dev/java/)
- [TestNG Documentation](https://testng.org/doc/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow Java Code Conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Write unit tests for new features

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

