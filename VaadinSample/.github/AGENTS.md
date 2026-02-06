# Agent Guidelines for VaadinSample

## Project Overview

**VaadinSample** is a Java 8 web application using Vaadin 7.7.17 framework, packaged as a WAR file and deployed via servlet container (Jetty). The project demonstrates Vaadin UI patterns: event-driven component composition, layout management, and client-server RPC communication through Vaadin's framework.

**Technology Stack:**
- Java 8 (source/target compatibility 1.8)
- Vaadin 7.7.17 (EOL framework - no longer receiving public security updates)
- Google Web Toolkit (GWT) for client-side widgetset compilation
- Maven build tool
- JUnit 5 (Jupiter) for testing
- Eclipse Jetty 9.4.54 for local development

## Build Commands

### Basic Build Operations
```bash
# Full build - compiles Java + GWT widgetset + creates WAR
mvn package

# Fast iteration - compile Java only (skips GWT widgetset compilation)
mvn compile

# Clean and rebuild widgetset (required after widgetset changes)
mvn clean vaadin:update-widgetset vaadin:compile

# Clean everything
mvn clean
```

### Testing
```bash
# Run all tests (17 unit tests in MyVaadinUITest)
mvn test

# Run a specific test class
mvn test -Dtest=MyVaadinUITest

# Run a specific test method
mvn test -Dtest=MyVaadinUITest#testButtonClickIncrementsCounter

# Skip tests during build
mvn package -DskipTests
```

### Running Locally
```bash
# Start Jetty server (configured in pom.xml)
mvn jetty:run

# Access application at: http://localhost:8080/
```

**Build Output:**
- WAR file: `target/VaadinSample-1.0.war`
- Compiled widgetsets: `src/main/webapp/VAADIN/widgetsets/`
- Test reports: `target/surefire-reports/`

## Code Style Guidelines

### Indentation & Formatting
- **Indentation:** 4 spaces (no tabs)
- **Line length:** Keep reasonable (~80-120 characters)
- **Encoding:** UTF-8 throughout
- **Spacing:** Use consistent spacing around operators and after commas

### Naming Conventions
- **Classes:** PascalCase (e.g., `MyVaadinUI`, `StreamResource`)
- **Methods/Variables:** camelCase (e.g., `clickCount`, `updateStatistics()`)
- **Constants:** UPPER_SNAKE_CASE with `static final` (e.g., `STATS_PANEL_WIDTH`, `DATE_FORMAT`)
- **Packages:** lowercase dot-separated (e.g., `com.nitinsurana.vaadinsample`)

### Imports
- **Organization:** Group imports logically - Vaadin classes first, Java standard library second
- **Wildcards:** Avoid wildcard imports; use explicit imports
- **Order:** Alphabetical within each group
- **Example:**
```java
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
```

### Types & Type Safety
- Use explicit types; avoid raw types
- Prefer `List<String>` over `List`
- Use generic types for collections: `new ArrayList<>()`
- No type inference (Java 8 doesn't have `var`)

### Documentation
- Add Javadoc to all public classes and methods
- Document non-obvious behavior, especially thread-safety concerns
- Include `@param` and `@return` tags where appropriate
- Example:
```java
/**
 * ThreadLocal to ensure thread-safety for SimpleDateFormat in multi-user web application
 */
private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ...
```

### Error Handling
- Use appropriate exception types
- Don't swallow exceptions silently
- Log errors when appropriate
- Clean up resources properly (use try-with-resources for streams)

## Vaadin-Specific Patterns

### UI Development
1. **Entry Point:** Extend `com.vaadin.ui.UI` and override `init(VaadinRequest)`
2. **Component Composition:** Build UIs declaratively using layouts and components
3. **Event Handling:** Use anonymous `ClickListener` implementations (legacy Vaadin 7 pattern)
   ```java
   button.addClickListener(new Button.ClickListener() {
       @Override
       public void buttonClick(ClickEvent event) {
           // Handle click
       }
   });
   ```
4. **Layout Hierarchy:** Use `VerticalLayout`/`HorizontalLayout` for containment
5. **Spacing:** Use `setMargin(true)` and `setSpacing(true)` for visual separation

### State Management
- Components hold UI state directly (no separate model layer in simple apps)
- Use private fields for state tracking (e.g., `clickCount`, `sessionStartTime`)
- Update UI components immediately when state changes

### Thread Safety
- Use `ThreadLocal` for non-thread-safe objects like `SimpleDateFormat`
- Synchronize access to shared collections (e.g., `clickTimestamps`)
- Take defensive copies before iterating over synchronized collections
- Example:
```java
List<Long> timestampsSnapshot;
synchronized (clickTimestamps) {
    timestampsSnapshot = new ArrayList<>(clickTimestamps);
}
```

### Constants & Configuration
- Extract UI dimensions and magic strings as constants
- Use descriptive names (e.g., `STATS_PANEL_WIDTH = "400px"`)
- Place constants at top of class

### File Downloads
- Use `FileDownloader` extension for button-based downloads
- Create `StreamResource` with dynamic `StreamSource` for generated content
- Set MIME type explicitly (e.g., `resource.setMIMEType("text/csv")`)
- Update resource before each download to reflect current data

## Testing Guidelines

### Test Framework
- Use JUnit 5 (Jupiter) with `@Test`, `@BeforeEach`, `@DisplayName`
- Place tests in `src/test/java/` mirroring source package structure
- Test class naming: `<ClassName>Test.java` (e.g., `MyVaadinUITest.java`)

### Test Organization
```java
@BeforeEach
public void setUp() {
    ui = new MyVaadinUI();
    request = null; // VaadinRequest can be null for basic tests
}

@Test
@DisplayName("Clear description of what test verifies")
public void testMethodName() {
    // Arrange - setup
    // Act - execute
    // Assert - verify
}
```

### Test Patterns
- Create helper methods to reduce duplication (e.g., `findButtonByCaption()`)
- Test both positive and negative cases
- Use descriptive assertion messages
- For timing tests, use `Thread.sleep()` with appropriate buffer (e.g., 1100ms for 1 second)
- Use regex patterns for validating dynamic content (timestamps, durations)

### Component Testing
```java
// Navigate component hierarchy
VerticalLayout layout = (VerticalLayout) ui.getContent();
Panel panel = (Panel) layout.getComponent(0);
Button button = findButtonByCaption(buttonLayout, "Click Me");

// Simulate user actions
button.click();

// Verify state
assertEquals(expected, actual, "Descriptive message");
assertTrue(condition, "What should be true");
```

## Architecture & Structure

### Package Structure
- Single package: `com.nitinsurana.vaadinsample`
- UI class and widgetset configuration must be in same package (Vaadin requirement)

### Key Files
- **Main UI:** `src/main/java/com/nitinsurana/vaadinsample/MyVaadinUI.java`
- **Widgetset:** `src/main/java/com/nitinsurana/vaadinsample/AppWidgetSet.gwt.xml`
- **Servlet Config:** `src/main/webapp/WEB-INF/web.xml`
- **Build Config:** `pom.xml`
- **Tests:** `src/test/java/com/nitinsurana/vaadinsample/MyVaadinUITest.java`

### GWT Widgetset Compilation
- Triggered automatically during `mvn package` via `vaadin-maven-plugin`
- Memory settings: `-Xmx512M -Xss1024k` for GWT compiler
- Output: `src/main/webapp/VAADIN/widgetsets/`
- For faster development: set `<draftCompile>true</draftCompile>` in pom.xml

## Common Tasks

### Adding a New Component
```java
// In MyVaadinUI.init() method
Label label = new Label("New content");
layout.addComponent(label);  // Add to layout, not DOM
```

### Adding a Button with Action
```java
Button newButton = new Button("Caption");
newButton.addClickListener(new Button.ClickListener() {
    @Override
    public void buttonClick(ClickEvent event) {
        // Handle action
    }
});
buttonLayout.addComponent(newButton);
```

### Troubleshooting Widgetset Issues
1. Verify widgetset class name matches `web.xml` configuration
2. Ensure `AppWidgetSet.gwt.xml` inherits correct module
3. Delete `target/` and `src/main/webapp/VAADIN/widgetsets/` directories
4. Rebuild: `mvn clean package`

## Important Limitations & Gotchas

- **Legacy Framework:** Vaadin 7 is EOL (use Vaadin 8+ for new projects)
- **Java 8 Only:** Project targets Java 1.8; no Java 9+ features
- **Production Mode:** Ensure `productionMode=true` in `web.xml` before deployment
- **Single Page:** Only one UI class; routing requires Vaadin Navigator or URL parameters
- **Security:** Known ReDoS vulnerability in EmailValidator (not used in this app)
- **GWT Compilation:** Can be slow; use `mvn compile` for Java-only changes during development

## Documentation Resources

Use the context7 MCP tool to reference Vaadin 7 and GWT documentation. Limit searches to 3 maximum per topic.

## Additional Documentation

- `.github/copilot-instructions.md` - GitHub Copilot specific instructions
- `SECURITY.md` - Security analysis and vulnerability assessment
- `docs/user-workflows.md` - User interaction sequence diagrams
