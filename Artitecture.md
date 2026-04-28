# TestFly SDK Architecture

## 1. Project Overview
**TestFly SDK** is a robust, scalable test automation framework designed for high-performance UI and API testing. Built on **Java 21**, it leverages **Playwright** for both browser interaction and API calls, ensuring speed and reliability (`auto-wait`, `tracing`, `video recording`).

The framework adopts a **hybrid** approach, supporting both:
- **TestNG** for standard modular testing.
- **Cucumber (BDD)** for behavior-driven scenarios with Gherkin syntax.

---

## 2. Technology Stack

| Component | Technology | Description |
|-----------|------------|-------------|
| **Core Language** | Java 21 | Modern Java features (Records, Pattern Matching). |
| **Build Tool** | Maven | Dependency and lifecycle management. |
| **UI Automation** | Playwright | Fast, reliable browser automation (Chromium, Firefox, WebKit). |
| **Test Runner** | TestNG | Parallel execution, listeners, data providers. |
| **BDD** | Cucumber 7 | Gherkin syntax for human-readable tests. |
| **API Testing** | Playwright APIRequest | Unified network layer for API testing. |
| **Configuration** | Owner API | Type-safe property management (`.properties`). |
| **Reporting** | Allure / Extent | Detailed test reports with screenshots, videos, and traces. |
| **Logging** | Log4j2 | Asynchronous, high-performance logging. |
| **Video Recording** | Playwright + ffmpeg | Automatic video capture for failed tests (.mp4). |
| **Data Driven** | CSV / Excel / JSON | Built-in data readers for parameterized tests. |

---

## 3. Core Architecture & Design Patterns

### 3.1. Browser Management (Thread-Safe Parallelism)
The framework uses a **ThreadLocal Strategy** in `BrowserManager` to ensure safely running tests in parallel.
- **Isolation**: Each thread gets its own `Playwright`, `Browser`, `BrowserContext`, and `Page` instance.
- **Lifecycle**:
    - `initializeDriver(browser)`: Starts the browser session with video recording and tracing.
    - `saveVideo(testName)`: Saves video as `.mp4` for failed tests (with ffmpeg conversion).
    - `discardVideo()`: Discards video for passed tests to save disk space.
    - `quitDriver()`: Safely closes page, context, and browser, removing ThreadLocals.
- **Video Recording Flow**:
    1. Context created with `setRecordVideoDir(1280x720)`.
    2. On failure → `page.close()` → `waitForVideoFile()` (stability check) → `.webm` → ffmpeg → `.mp4`.
    3. On success → `.webm` silently deleted.
- **Playwright Tracing**:
    - `startTracing()`: Begins recording screenshots, snapshots, and sources.
    - On failure → `stopTracing(name)`: Saves `.zip` trace file.
    - On success → `stopAndDiscardTracing()`: Discards trace.

### 3.2. Web Actions (Composition Pattern)
Web interaction classes are organized as **granular, composable actions** rather than a deep inheritance hierarchy.
- **`WebActions`**: Composite facade that delegates to specialized action classes:
    - `MouseActions` (Click, Hover, Drag & Drop)
    - `KeyboardActions` (Type, Press, Clear)
    - `WaitActions` (Smart waits: visible, clickable, navigated)
    - `ElementChecks` (Assertions: `isVisible`, `hasText`)
    - `FormActions` (Select, Checkbox, Radio)
    - `NavigationActions` (Navigate, Back, Forward, Reload)
    - `AlertActions` (Accept, Dismiss, GetText)
    - `CookieActions` (Get, Set, Delete)
    - `JavaScriptActions` (Execute JS, Scroll)
    - `ScreenActions` (Screenshot, Highlight)
- **`BasePage`**: Extends `WebActions`, provides page object pattern with ready-to-use actions.
- **Benefit**: Clean, readable test code — `click(selector)` instead of `driver.findElement(...).click()`.

### 3.3. API Engine (Wrapper Pattern)
API testing is built directly on Playwright's `APIRequestContext`, offering faster execution than HTTPClient-based libraries.
- **`ApiEngine`**: Wraps network calls, handles serialization and response parsing.
- **`ApiActions`**: Fluent interface for building requests (Headers, Query Params, Body).
- **`ApiAssertions`**: Response validation (status, body, headers, JSON path).
- **`ApiContext`**: Manages `APIRequestContext` lifecycle with ThreadLocal isolation.
- **Features**:
    - **Auto-Retry**: Configurable retry logic for flaky endpoints.
    - **Allure Integration**: Automatically attaches request/response details to reports.

### 3.4. Configuration Management
Uses the **Owner** library (`ConfigManager.java`) to map `config.properties` and environment-specific files to Java interfaces.
- **Type Safety**: `ConfigManager.get().timeout()` returns typed values.
- **Environment Support**: `dev.properties`, `stage.properties`, `preprod.properties` overlays.
- **Key Configurations**:
    - `browser`: chromium / firefox / webkit
    - `headless`: true / false
    - `record.video`: true / false (video recording toggle)
    - `record.trace`: true / false (Playwright trace toggle)
    - `timeout`: element wait timeout (ms)

### 3.5. BDD Hooks (Lifecycle Management)
Cucumber hooks manage test lifecycle automatically based on tags:

| Hook | Tags | Description |
|------|------|-------------|
| `GlobalHooks` | All | `@BeforeAll` creates directories, `@AfterAll` disposes Playwright |
| `WebHooks` | `@ui`, `@web` | Browser init, screenshot on fail, video save on fail |
| `ApiHooks` | `@api` | API context init, response logging |

**Failure Handling Flow** (`WebHooks`):
```
Test FAILS
  ├── stopTracing() → save trace .zip
  ├── takeScreenshotBytes() → save screenshot .png
  ├── saveVideo() → wait for stabilize → .webm → ffmpeg → .mp4
  └── attachVideoToAllure() → single Allure attachment (video/mp4)
```

---

## 4. Project Directory Structure

```text
src/main/java/com/testfly/sdk/
├── api/
│   ├── actions/          # ApiActions, ApiAssertions, ApiBaseActions, ApiContext
│   ├── base/             # BaseApiTest
│   └── engine/           # ApiEngine, ApiResponse
├── bdd/
│   └── hooks/            # Cucumber hooks: GlobalHooks, WebHooks, ApiHooks
├── context/              # ScenarioContext (share state between steps)
├── core/                 # Core managers
│   ├── ApiManager.java       # API request context manager
│   ├── BrowserFactory.java   # Browser launch factory (Chromium, Firefox, WebKit)
│   ├── BrowserManager.java   # ThreadLocal browser/page lifecycle + video + tracing
│   ├── ConfigManager.java    # Owner-based config interface
│   ├── EnvConfig.java        # Environment-specific config
│   ├── LogManager.java       # Log4j2 wrapper
│   └── PlaywrightManager.java # Singleton Playwright instance (lifecycle)
├── data/                 # Data readers
│   ├── CsvReader.java
│   ├── ExcelReader.java
│   └── JsonReader.java
├── exceptions/           # Custom exceptions
│   ├── ApiRequestException.java
│   ├── BrowserInitializationException.java
│   ├── ElementNotInteractableException.java
│   ├── FrameworkException.java
│   ├── RetryExhaustedException.java
│   └── ValidationException.java
├── reporting/
│   └── listeners/        # TestNG listeners
│       ├── ApiTestListener.java
│       ├── ExecutionListener.java
│       ├── ExtentReportListener.java
│       ├── RetryAnalyzer.java
│       └── WebTestListener.java
├── utils/                # Utility classes
│   ├── DateUtils.java
│   ├── FileUtils.java
│   ├── RandomUtils.java
│   └── StringUtils.java
└── web/
    ├── actions/          # Granular web action classes
    │   ├── AlertActions.java
    │   ├── CookieActions.java
    │   ├── ElementChecks.java
    │   ├── FormActions.java
    │   ├── JavaScriptActions.java
    │   ├── KeyboardActions.java
    │   ├── MouseActions.java
    │   ├── NavigationActions.java
    │   ├── PageContext.java
    │   ├── ScreenActions.java
    │   ├── WaitActions.java
    │   └── WebActions.java     # Composite facade
    └── base/              # BasePage, BaseWebTest

src/test/java/com/testfly/sdk/
├── tests/
│   ├── api/              # API step definitions (ApiSteps.java)
│   ├── runners/          # Cucumber TestNG runners (ApiTestRunner, WebTestRunner)
│   └── web/              # Web step definitions + page objects (WebSteps, LoginPage)

src/test/resources/
├── features/             # Cucumber feature files (api.feature, web.feature)
├── testng/               # TestNG suite XML files (testng.xml, testng-api.xml, testng-web.xml)
├── allure.properties     # Allure configuration
├── cucumber.properties   # Cucumber configuration
├── extent.properties     # ExtentReports configuration
├── extent-config.xml     # ExtentReports theme config
└── extent-cucumber.xml   # Extent-Cucumber adapter config

src/main/resources/
├── config.properties     # Main framework configuration
├── dev.properties        # Dev environment config
├── stage.properties      # Stage environment config
├── preprod.properties    # Pre-prod environment config
└── log4j2.xml            # Logging configuration
```

---

## 5. Execution Flow

### Web Tests (Cucumber + TestNG)
1. **TestNG Suite** (`testng-web.xml`) → `WebTestRunner` (extends `AbstractTestNGCucumberTests`)
2. **`@BeforeAll` (GlobalHooks)**: Creates `target/` directories (screenshots, videos, traces, allure-results)
3. **`@Before` (WebHooks)**: `BrowserManager.initializeDriver()` → launches browser, starts video + tracing
4. **Step Execution**: `WebSteps` delegates to `LoginPage` (extends `BasePage` → `WebActions`)
5. **`@After` (WebHooks)**:
    - **On Failure**: Save trace → take screenshot → save video (.mp4) → attach all to Allure
    - **On Success**: Discard trace → discard video
6. **`BrowserManager.quitDriver()`**: Close page → context → browser
7. **`@AfterAll` (GlobalHooks)**: `PlaywrightManager.dispose()`

### API Tests (Cucumber + TestNG)
1. **TestNG Suite** (`testng-api.xml`) → `ApiTestRunner`
2. **`@Before` (ApiHooks)**: Initialize API request context
3. **Step Execution**: `ApiSteps` uses `ApiActions` for requests and `ApiAssertions` for validation
4. **`@After` (ApiHooks)**: Log response details, dispose context

### Parallel Execution
- **Web**: `@DataProvider(parallel = true)` in runners + ThreadLocal in `BrowserManager`
- **API**: ThreadLocal `APIRequestContext` via `ApiManager`
- **Config**: `parallel.count` in `config.properties` controls thread pool size

---

## 6. Reporting & Artifacts

### Allure Report
- **Screenshots**: Attached automatically on failure (`.png`)
- **Videos**: Attached automatically on failure (`.mp4`, via ffmpeg conversion)
- **Traces**: Playwright trace files (`.zip`) — viewable in [Trace Viewer](https://trace.playwright.dev)
- **API Logs**: Request/response details attached for API tests

### Output Directory Structure
```text
target/
├── screenshots/          # Failure screenshots (.png)
├── videos/               # Failure videos (.mp4)
├── traces/               # Playwright traces (.zip)
├── allure-results/       # Allure raw results
├── extent-reports/       # ExtentReports HTML
├── surefire-reports/     # TestNG/JUnit XML reports
└── logs/                 # Log4j2 output files
```

---

## 7. Key Advantages
1. **Speed**: Playwright's WebSocket connection is significantly faster than WebDriver's HTTP protocol.
2. **Stability**: Built-in `auto-wait` reduces flaky tests — no `Thread.sleep` needed.
3. **Readability**: Cucumber Gherkin syntax + composable actions = human-readable tests.
4. **Traceability**: Full Allure reporting with screenshots, videos, and traces for every failure.
5. **Parallel-Ready**: ThreadLocal architecture ensures safe parallel execution out of the box.
6. **Video Evidence**: Automatic `.mp4` video recording for failed tests, with zero manual intervention.
7. **Type-Safe Config**: Owner library eliminates string-based property lookups.

---

## 8. Recommendations
- **Containerization**: Add a `Dockerfile` for CI pipeline execution.
- **CI/CD Integration**: Add GitHub Actions / Jenkins pipeline configurations.
- **Cross-Browser Matrix**: Extend parallel execution to test Chromium, Firefox, and WebKit simultaneously.
- **Performance Testing**: Integrate Playwright's performance metrics collection.
- **Visual Regression**: Add screenshot comparison for visual regression testing.
- **Test Data Management**: Centralize test data with Faker or dedicated test data generators.