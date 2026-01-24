# Contributing to TestFly SDK

Thank you for your interest in contributing to TestFly SDK! We welcome contributions from the community and are excited to have you join us.

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When creating a bug report, include:

- **Clear title** describing the issue
- **Detailed description** of the problem
- **Steps to reproduce** the issue
- **Expected behavior**
- **Actual behavior**
- **Screenshots** if applicable
- **Environment details** (Java version, OS, browser, etc.)

### Suggesting Enhancements

Enhancement suggestions are greatly appreciated! Please provide:

- **Clear title** for the enhancement
- **Detailed description** of the proposed feature
- **Use cases** for the feature
- **Examples** of how you envision it working

### Pull Requests

1. **Fork** the repository
2. **Create a branch** for your feature or bugfix:
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit** your changes with a clear commit message:
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push** to your branch:
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Create a Pull Request** with a clear description of your changes

## Development Guidelines

### Code Style

- Follow Java Code Conventions
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Keep methods short and focused
- Write unit tests for new features

### Commit Messages

Use clear, descriptive commit messages:

- `feat:` for new features
- `fix:` for bug fixes
- `docs:` for documentation changes
- `style:` for code style changes (formatting, etc.)
- `refactor:` for code refactoring
- `test:` for adding or updating tests
- `chore:` for maintenance tasks

Example:
```
feat: add support for parallel test execution
fix: resolve memory leak in DriverManager
docs: update installation guide
```

### Testing

- Write unit tests for new functionality
- Ensure all existing tests pass
- Test on different browsers when making UI changes
- Add integration tests for new features

## Project Structure

Understanding the project structure will help you contribute effectively:

```
src/main/java/com/testfly/sdk/
├── actions/       # UI action interfaces
├── api/           # API testing components
├── base/          # Base classes
├── config/        # Configuration
├── context/       # Scenario context
├── data/          # Data readers
├── exceptions/    # Custom exceptions
├── hooks/         # Cucumber hooks
├── listeners/     # TestNG listeners
├── manager/       # Manager classes
└── utils/         # Utility classes
```

## Getting Started

### Setup Development Environment

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/TestFlySdk.git
   cd TestFlySdk
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

3. Install Playwright browsers:
   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
   ```

4. Run tests to verify setup:
   ```bash
   mvn test
   ```

## Coding Standards

### Java Code Style

- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Place opening braces on the same line
- Use meaningful names for classes, methods, and variables

### Example:

```java
public class ExampleService {
    
    private static final Logger logger = LogManager.getLogger(ExampleService.class);
    
    public void performAction(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        // Implementation
    }
}
```

### Documentation

- Add Javadoc comments for all public APIs
- Include parameter descriptions
- Describe return values and exceptions
- Provide usage examples in complex cases

Example:

```java
/**
 * Performs an action with the specified input.
 *
 * @param input the input string to process
 * @throws IllegalArgumentException if input is null or empty
 * @return the result of the action
 */
public String performAction(String input) {
    // Implementation
}
```

## Review Process

All pull requests undergo review before merging:

1. **Automated checks**: CI pipeline runs tests and code quality checks
2. **Code review**: Maintainers review your code
3. **Feedback**: You may receive comments or suggestions
4. **Approval**: Once approved, your PR will be merged

## Community Guidelines

### Be Respectful

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on what is best for the community
- Show empathy toward other community members

### Communication

- Use clear and concise language
- Provide context in discussions
- Be patient with different time zones
- Assume positive intent

## Questions?

If you have questions about contributing:

- Check existing issues and discussions
- Read the [README.md](README.md) and [Architecture.md](Artitecture.md)
- Contact maintainers via GitHub issues

## Recognition

Contributors are recognized in:
- The project's contributors list
- Release notes for significant contributions
- Project documentation for major features

## License

By contributing to TestFly SDK, you agree that your contributions will be licensed under the [MIT License](LICENSE).

---

Thank you for contributing to TestFly SDK! 🚀
