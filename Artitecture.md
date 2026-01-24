# TestFly SDK Architecture Analysis

## 1. Project Overview
**TestFly SDK** is a robust, scalable test automation framework designed for high-performance UI and API testing. Built on **Java 21**, it leverages **Playwright** for both browser interaction and API calls, ensuring speed and reliability (`auto-wait`, `tracing`).

The framework adopts a **hybrid** approach, supporting both:
- **TestNG** for standard modular testing.
- **Cucumber (BDD)** for behavior-driven scenarios.

## 2. Technology Stack
| Component | Technology | Description |
|-----------|------------|-------------|
| **Core Language** | Java 21 | Modern Java features (Records, Pattern Matching, Virtual Threads ready). |
| **Build Tool** | Maven | Dependency and lifecycle management. |
| **UI Automation** | Playwright | Fast, reliable browser automation (Chromium, Firefox, WebKit). |
| **Test Runner** | TestNG | Parellel execution, listeners, data providers. |
| **BDD** | Cucumber | Gherkin syntax for human-readable tests. |
| **API Testing** | Playwright API | Unified network layer for API testing (replaces RestAssured). |
| **Configuration** | Owner API | Type-safe property management (`.properties`). |
| **Reporting** | Allure / Extent | Detailed testing reports with history and attachments. |
| **Logging** | Log4j2 | Asynchronous, high-performance logging. |
| **Utils** | Lombok | Boilerplate reduction. |

---

## 3. Core Architecture & Design Patterns

### 3.1. Driver Management (Thread-Safe Parallelism)
The framework uses a **ThreadLocal Strategy** in `DriverManager` to ensure safely running tests in parallel.
- **Isolation**: Each thread gets its own `Playwright`, `BrowserContext`, and `Page` instance.
- **Lifecycle**:
    - `initializeDriver(browser)`: Starts the browser session.
    - `quitDriver()`: Safely closes context and page, removing them from the thread.
- **Configuration**: Dynamic browser selection (Chrome, Firefox, etc.) via `testng.xml` parameters or `ConfigManager`.

### 3.2. Interface-Based Action Composition (The "Role" Pattern)
Unlike traditional inheritance-heavy frameworks, TestFly SDK uses **Interface Composition** for actions.
- **`IWebActions`**: A composite interface that extends granular capabilities:
    - `IMouseActions` (Click, Hover)
    - `IKeyboardActions` (Type, Press)
    - `IWaitActions` (Smart waits)
    - `IElementChecks` (Assertions: `isVisible`, `hasText`)
- **Benefit**: `BasePage` and `BaseTest` implement `IWebActions`. This allows writing code like `click(selector)` directly in tests/pages without `driver.find...` boilerplate, keeping the syntax clean and readable.

### 3.3. API Engine (Wrapper Pattern)
API testing is built directly on Playwright's `APIRequestContext`, offering 30-40% faster execution than HTTPClient-based libraries.
- **`ApiEngine`**: Wraps the network calls, handling serialization (Gson) and response parsing.
- **`BaseApi`**: Provides a fluent interface for setting Headers, Query Params, and Body data using `ThreadLocal` storage.
- **Features**:
    - **Auto-Retry**: Configurable retry logic for flaky endpoints.
    - **Allure Integration**: Automatically attaches Req/Res details to the report.

### 3.4. Configuration Management
Uses the **Owner** library (`ConfigManager.java`) to map `config.properties` and environment-specific files (e.g., `dev.properties`) to Java interfaces.
- **Type Safety**: No more `getProperty("key")` strings; use `Config.get().timeout()` (int).
- **Flexibility**: Supports merging properties and safe defaults.

---

## 4. Project Directory Structure

```text
src/main/java/com/testfly/sdk/
├── actions/        # Granular interfaces for UI interactions (IWebActions, etc.)
├── api/            # API Engine and Response wrappers
├── base/           # Base classes (BaseTest, BasePage, BaseApi)
├── config/         # Owner interfaces for property management
├── context/        # ScenarioContext for sharing state between steps
├── manager/        # DriverManager, ApiContextManager (ThreadLocal handling)
├── hooks/          # Cucumber Hooks
├── listeners/      # TestNG Listeners
└── utils/          # Helper utilities

src/test/java/com/testfly/sdk/
├── tests/          # Test implementations
└── steps/          # Cucumber Step Definitions
```

## 5. Execution Flow

### UI Tests (TestNG)
1. **`BeforeMethod` (`BaseTest`)**:
    - Reads browser config.
    - Initializes `DriverManager`.
    - Clears `ScenarioContext`.
2. **Test Execution**:
    - Test method runs using `IWebActions` methods.
    - Interacts with Page Objects extending `BasePage`.
3. **`AfterMethod`**:
    - Checks status.
    - **On Failure**: Takes a screenshot via `Playwright`.
    - **Teardown**: Quits driver and clears ThreadLocals.

### API Tests
1. **`ApiContextManager`**: Initializes a lightweight, headless network context.
2. **`BaseApi`**: Builds the request (Headers, Body).
3. **`ApiEngine`**: Executes the request, expects `ApiResponse`.
4. **Validation**: Assertions performed on the `ApiResponse` object.

## 6. Key Advantages
1.  **Speed**: Playwright's WebSocket connection is significantly faster than WebDriver's HTTP JSON Wire Protocol.
2.  **Stability**: built-in `auto-wait` reduces the need for explicit waits (`Thread.sleep`).
3.  **Readability**: The Action Interface pattern makes test code look like natural language.
4.  **Traceability**: Full Allure reporting with attachments for every step.

## 7. Recommendations
- **RestAssured Cleanup**: Remove RestAssured dependencies from `pom.xml` (version 5.5.0) as the codebase exclusively uses Playwright's APIRequestContext for API testing. This will reduce artifact size by ~2MB.
- **Containerization**: Add a `Dockerfile` to easily run these tests in CI pipelines.
- **Allure Report Integration**: Enhance Allure integration by adding `allure-commandline` to pom.xml and adding maven-surefire-plugin configuration for automatic report generation.
- **Parallel Execution**: Document parallel test execution strategies for different browser configurations.
