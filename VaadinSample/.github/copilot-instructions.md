# Copilot Instructions for VaadinSample

## Project Overview

**VaadinSample** is a traditional Java web application using Vaadin 7.0.5, packaged as a WAR file and deployed via Jetty/servlet container. The project demonstrates basic Vaadin UI patterns: event-driven component composition, layout management, and client-server communication through Vaadin's RPC framework.

## Architecture

### Core Structure
- **UI Entry Point**: [MyVaadinUI.java](src/main/java/com/nitinsurana/vaadinsample/MyVaadinUI.java) extends `com.vaadin.ui.UI` and implements `init(VaadinRequest)` - this is the single-page application root
- **Widgetset Configuration**: [AppWidgetSet.gwt.xml](src/main/java/com/nitinsurana/vaadinsample/AppWidgetSet.gwt.xml) declares the GWT widget compilation module (inherits `DefaultWidgetSet`)
- **Servlet Mapping**: [web.xml](src/main/webapp/WEB-INF/web.xml) configures `VaadinServlet` to route all requests (`/*`) to `MyVaadinUI`

### Build & Runtime
- **Build Tool**: Maven with Vaadin plugin for GWT widgetset compilation
- **Deployment**: WAR package runs on servlet container (Jetty configured in pom.xml)
- **Java Version**: Target 1.6 (legacy requirement)
- **Key Dependencies**: vaadin-server, vaadin-client-compiled, vaadin-themes (v7.0.5)

## Developer Workflows

### Build & Compile
```bash
# Full package (compiles widgetset + WAR)
mvn package

# Fast iteration - compile Java only (skips GWT widgetset)
mvn compile

# Clean widgetset artifacts before recompile
mvn clean vaadin:update-widgetset vaadin:compile
```

### Running Locally
- Jetty plugin configured in pom.xml; check `maven-plugin.xml` for `jetty-maven-plugin` configuration
- WAR deploys to `/` context root by default
- Access via `http://localhost:8080/` after deployment

### GWT Widgetset Compilation
- Triggered automatically during `mvn package` via `vaadin-maven-plugin`
- Output location: `src/main/webapp/VAADIN/widgetsets/`
- Performance notes: extraJvmArgs set to `-Xmx512M -Xss1024k` for GWT memory; draftCompile disabled (use `<draftCompile>true</draftCompile>` to speed up development iterations)
- Cleanup: maven-clean-plugin removes widgetsets directory before rebuilds (prevents stale artifacts)

## Key Patterns & Conventions

### Vaadin UI Development
1. **Component Composition**: Build UIs declaratively (e.g., VerticalLayout with buttons and labels) - avoid manual DOM manipulation
2. **Event Handling**: Use anonymous `ClickListener` implementations (legacy Vaadin 7 pattern; note: newer Vaadin uses lambdas)
3. **Layout Hierarchy**: `VerticalLayout` / `HorizontalLayout` for containment; `setMargin(true)` is common for spacing
4. **State Management**: Components hold state; no explicit model layer in simple apps like this

### Package Structure
- Single package: `com.nitinsurana.vaadinsample`
- Convention: UI class + Widgetset module in same package (ensures widgetset discovery by servlet)

## Integration Points & Dependencies

### Vaadin Framework Communication
- Client-Server RPC: Handled transparently by Vaadin; button clicks trigger server-side `ClickListener` callbacks
- Widgetset: GWT-compiled JavaScript running in browser communicates with Java servlet via UIDL protocol

### Servlet Container Contract
- `VaadinServlet` handles all HTTP requests under mapped path
- Expects UI class specified in `web.xml` init-param
- Session management automatic

## Common Tasks & Quick Fixes

### Add a New UI Component
Update [MyVaadinUI.init()](src/main/java/com/nitinsurana/vaadinsample/MyVaadinUI.java) method:
```java
Label label = new Label("New content");
layout.addComponent(label);  // Add to layout, not DOM
```

### Rebuild After UI Changes
```bash
mvn compile  # Just recompile Java, skip GWT for faster iteration
mvn package  # Full build when ready to test in browser
```

### Troubleshoot Widgetset Issues
1. Check that widgetset class name in code matches `web.xml` param-value
2. Ensure [AppWidgetSet.gwt.xml](src/main/java/com/nitinsurana/vaadinsample/AppWidgetSet.gwt.xml) inherits correct module
3. Delete `target/` and `src/main/webapp/VAADIN/widgetsets/` directories, rebuild

## Important Limitations & Gotchas

- **Java 6 Compatibility**: Target Java 1.6; avoid Java 7+ syntax
- **No Custom Widgets**: Default widgetset only; custom GWT widgets require advanced GWT knowledge
- **Single Page App**: Only one UI class; routing/multiple pages require Vaadin Navigator or URL parameters
- **Production Mode**: Set `productionMode=true` in [web.xml](src/main/webapp/WEB-INF/web.xml) before deployment
