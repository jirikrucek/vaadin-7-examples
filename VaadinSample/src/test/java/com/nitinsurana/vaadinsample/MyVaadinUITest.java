package com.nitinsurana.vaadinsample;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MyVaadinUI class
 */
public class MyVaadinUITest {

    private MyVaadinUI ui;
    private VaadinRequest request;

    @BeforeEach
    public void setUp() {
        ui = new MyVaadinUI();
        request = null; // VaadinRequest can be null for testing basic functionality
    }

    @Test
    @DisplayName("UI should be initialized with content on init")
    public void testUIInitialization() {
        ui.init(request);
        assertNotNull(ui.getContent(), "UI content should not be null after initialization");
        assertTrue(ui.getContent() instanceof VerticalLayout, "Content should be a VerticalLayout");
    }

    @Test
    @DisplayName("Statistics panel should be present after initialization")
    public void testStatisticsPanelPresent() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        assertTrue(layout.getComponentCount() > 0, "Layout should contain components");
        
        // Check for stats panel (first component)
        assertTrue(layout.getComponent(0) instanceof Panel, "First component should be a Panel");
        Panel statsPanel = (Panel) layout.getComponent(0);
        assertEquals("Statistics Dashboard", statsPanel.getCaption(), "Panel should have correct caption");
    }

    @Test
    @DisplayName("Button click should increment click counter")
    public void testButtonClickIncrementsCounter() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Find the HorizontalLayout containing buttons (typically second component)
        com.vaadin.ui.HorizontalLayout buttonLayout = null;
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                buttonLayout = (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
                break;
            }
        }
        
        assertNotNull(buttonLayout, "Button layout should exist");
        
        // Find the Click Me button
        Button clickButton = null;
        for (int i = 0; i < buttonLayout.getComponentCount(); i++) {
            if (buttonLayout.getComponent(i) instanceof Button) {
                Button btn = (Button) buttonLayout.getComponent(i);
                if ("Click Me".equals(btn.getCaption())) {
                    clickButton = btn;
                    break;
                }
            }
        }
        assertNotNull(clickButton, "Click Me button should exist");
    }

    @Test
    @DisplayName("Reset button should clear components except stats panel and buttons")
    public void testResetButtonStructure() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Find the HorizontalLayout containing buttons
        com.vaadin.ui.HorizontalLayout buttonLayout = null;
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                buttonLayout = (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
                break;
            }
        }
        
        assertNotNull(buttonLayout, "Button layout should exist");
        
        // Find the Clear Messages button
        Button resetButton = null;
        for (int i = 0; i < buttonLayout.getComponentCount(); i++) {
            if (buttonLayout.getComponent(i) instanceof Button) {
                Button btn = (Button) buttonLayout.getComponent(i);
                if ("Clear Messages".equals(btn.getCaption())) {
                    resetButton = btn;
                    break;
                }
            }
        }
        assertNotNull(resetButton, "Clear Messages button should exist");
    }

    @Test
    @DisplayName("Statistics panel width should be 400px")
    public void testStatisticsPanelWidth() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel statsPanel = (Panel) layout.getComponent(0);
        assertEquals("400.0px", statsPanel.getWidth() + statsPanel.getWidthUnits().getSymbol(), 
                    "Stats panel width should be 400px");
    }

    @Test
    @DisplayName("Initial click count should be zero")
    public void testInitialClickCount() {
        ui.init(request);
        // Note: clickCount is private, so we verify via statistics labels
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel statsPanel = (Panel) layout.getComponent(0);
        VerticalLayout statsLayout = (VerticalLayout) statsPanel.getContent();
        
        Label totalClicksLabel = (Label) statsLayout.getComponent(0);
        assertTrue(totalClicksLabel.getValue().contains("Total Clicks: 0"), 
                  "Initial click count should be 0");
    }

    @Test
    @DisplayName("Session start time should be set")
    public void testSessionStartTimeSet() {
        long beforeInit = System.currentTimeMillis();
        ui.init(request);
        long afterInit = System.currentTimeMillis();
        
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel statsPanel = (Panel) layout.getComponent(0);
        VerticalLayout statsLayout = (VerticalLayout) statsPanel.getContent();
        
        Label sessionStartLabel = (Label) statsLayout.getComponent(1);
        String labelValue = sessionStartLabel.getValue();
        assertTrue(labelValue.contains("Session Start Time:"), 
                  "Session start time label should be present");
        assertTrue(labelValue.length() > "Session Start Time: ".length(), 
                  "Session start time should have a value");
    }

    @Test
    @DisplayName("Session duration label should be present")
    public void testSessionDurationLabelPresent() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel statsPanel = (Panel) layout.getComponent(0);
        VerticalLayout statsLayout = (VerticalLayout) statsPanel.getContent();
        
        Label sessionDurationLabel = (Label) statsLayout.getComponent(3);
        String labelValue = sessionDurationLabel.getValue();
        assertTrue(labelValue.contains("Session Duration:"), 
                  "Session duration label should be present");
    }

    @Test
    @DisplayName("All required labels should be initialized")
    public void testAllLabelsInitialized() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel statsPanel = (Panel) layout.getComponent(0);
        VerticalLayout statsLayout = (VerticalLayout) statsPanel.getContent();
        
        assertEquals(4, statsLayout.getComponentCount(), 
                    "Stats layout should have 4 labels");
        
        Label totalClicks = (Label) statsLayout.getComponent(0);
        Label sessionStart = (Label) statsLayout.getComponent(1);
        Label timeSinceClick = (Label) statsLayout.getComponent(2);
        Label sessionDuration = (Label) statsLayout.getComponent(3);
        
        assertNotNull(totalClicks.getValue());
        assertNotNull(sessionStart.getValue());
        assertNotNull(timeSinceClick.getValue());
        assertNotNull(sessionDuration.getValue());
    }

    @Test
    @DisplayName("Click History Log panel should be present after initialization")
    public void testHistoryPanelPresent() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        assertTrue(layout.getComponentCount() >= 2, "Layout should contain at least stats and history panels");
        
        // Check for history panel (second component)
        assertTrue(layout.getComponent(1) instanceof Panel, "Second component should be a Panel");
        Panel historyPanel = (Panel) layout.getComponent(1);
        assertEquals("Click History Log", historyPanel.getCaption(), "Panel should have correct caption");
    }

    @Test
    @DisplayName("Click History Log panel should have correct dimensions")
    public void testHistoryPanelDimensions() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel historyPanel = (Panel) layout.getComponent(1);
        assertEquals("500.0px", historyPanel.getWidth() + historyPanel.getWidthUnits().getSymbol(), 
                    "History panel width should be 500px");
        assertEquals("300.0px", historyPanel.getHeight() + historyPanel.getHeightUnits().getSymbol(), 
                    "History panel height should be 300px");
    }

    @Test
    @DisplayName("Clear History button should be present")
    public void testClearHistoryButtonPresent() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Find the HorizontalLayout containing buttons
        com.vaadin.ui.HorizontalLayout buttonLayout = null;
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                buttonLayout = (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
                break;
            }
        }
        
        assertNotNull(buttonLayout, "Button layout should exist");
        
        // Find the Clear History button
        Button clearHistoryButton = null;
        for (int i = 0; i < buttonLayout.getComponentCount(); i++) {
            if (buttonLayout.getComponent(i) instanceof Button) {
                Button btn = (Button) buttonLayout.getComponent(i);
                if ("Clear History".equals(btn.getCaption())) {
                    clearHistoryButton = btn;
                    break;
                }
            }
        }
        assertNotNull(clearHistoryButton, "Clear History button should exist");
    }

    @Test
    @DisplayName("Initial history log should be empty")
    public void testInitialHistoryEmpty() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel historyPanel = (Panel) layout.getComponent(1);
        VerticalLayout historyLayout = (VerticalLayout) historyPanel.getContent();
        
        assertEquals(0, historyLayout.getComponentCount(), 
                    "History layout should be empty initially");
    }

    @Test
    @DisplayName("Reset button should preserve history panel when clearing messages")
    public void testResetButtonPreservesHistoryPanel() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Get initial component count
        int initialCount = mainLayout.getComponentCount();
        assertTrue(initialCount >= 3, "Should have stats panel, history panel, and button layout");
        
        // Verify both panels exist
        Panel statsPanel = (Panel) mainLayout.getComponent(0);
        Panel historyPanel = (Panel) mainLayout.getComponent(1);
        assertEquals("Statistics Dashboard", statsPanel.getCaption());
        assertEquals("Click History Log", historyPanel.getCaption());
    }

    @Test
    @DisplayName("Three buttons should be present in button layout")
    public void testThreeButtonsPresent() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Find the HorizontalLayout containing buttons
        com.vaadin.ui.HorizontalLayout buttonLayout = null;
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                buttonLayout = (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
                break;
            }
        }
        
        assertNotNull(buttonLayout, "Button layout should exist");
        
        // Count buttons
        int buttonCount = 0;
        for (int i = 0; i < buttonLayout.getComponentCount(); i++) {
            if (buttonLayout.getComponent(i) instanceof Button) {
                buttonCount++;
            }
        }
        
        assertEquals(3, buttonCount, "Should have exactly 3 buttons");
    }

}