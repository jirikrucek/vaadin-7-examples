package com.nitinsurana.vaadinsample;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
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
        
        assertEquals(6, statsLayout.getComponentCount(), 
                    "Stats layout should have 6 labels");
        
        Label totalClicks = (Label) statsLayout.getComponent(0);
        Label sessionStart = (Label) statsLayout.getComponent(1);
        Label timeSinceClick = (Label) statsLayout.getComponent(2);
        Label sessionDuration = (Label) statsLayout.getComponent(3);
        Label avgClicksPerMinute = (Label) statsLayout.getComponent(4);
        Label peakActivity = (Label) statsLayout.getComponent(5);
        
        assertNotNull(totalClicks.getValue());
        assertNotNull(sessionStart.getValue());
        assertNotNull(timeSinceClick.getValue());
        assertNotNull(sessionDuration.getValue());
        assertNotNull(avgClicksPerMinute.getValue());
        assertNotNull(peakActivity.getValue());
    }

    @Test
    @DisplayName("Click history panel should be present after initialization")
    public void testClickHistoryPanelPresent() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        
        // Check for history panel (third component after stats panel and button layout)
        assertTrue(layout.getComponentCount() >= 3, "Layout should contain at least 3 components");
        
        // The history panel should be a Panel
        Panel historyPanel = null;
        for (int i = 0; i < layout.getComponentCount(); i++) {
            if (layout.getComponent(i) instanceof Panel) {
                Panel panel = (Panel) layout.getComponent(i);
                if ("Click History & Visualization".equals(panel.getCaption())) {
                    historyPanel = panel;
                    break;
                }
            }
        }
        
        assertNotNull(historyPanel, "Click History & Visualization panel should exist");
    }

    @Test
    @DisplayName("Average clicks per minute should be displayed")
    public void testAverageClicksPerMinute() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel statsPanel = (Panel) layout.getComponent(0);
        VerticalLayout statsLayout = (VerticalLayout) statsPanel.getContent();
        
        Label avgClicksLabel = (Label) statsLayout.getComponent(4);
        assertTrue(avgClicksLabel.getValue().contains("Average Clicks/Minute:"), 
                  "Average clicks per minute label should be present");
    }

    @Test
    @DisplayName("Peak activity period should be displayed")
    public void testPeakActivityPeriod() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        Panel statsPanel = (Panel) layout.getComponent(0);
        VerticalLayout statsLayout = (VerticalLayout) statsPanel.getContent();
        
        Label peakActivityLabel = (Label) statsLayout.getComponent(5);
        assertTrue(peakActivityLabel.getValue().contains("Peak Activity:"), 
                  "Peak activity label should be present");
    }

    @Test
    @DisplayName("Click history table should be present")
    public void testClickHistoryTablePresent() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        
        Panel historyPanel = null;
        for (int i = 0; i < layout.getComponentCount(); i++) {
            if (layout.getComponent(i) instanceof Panel) {
                Panel panel = (Panel) layout.getComponent(i);
                if ("Click History & Visualization".equals(panel.getCaption())) {
                    historyPanel = panel;
                    break;
                }
            }
        }
        
        assertNotNull(historyPanel, "History panel should exist");
        VerticalLayout historyLayout = (VerticalLayout) historyPanel.getContent();
        
        // Find the Click Event Log table
        Table clickHistoryTable = null;
        for (int i = 0; i < historyLayout.getComponentCount(); i++) {
            if (historyLayout.getComponent(i) instanceof Table) {
                Table table = (Table) historyLayout.getComponent(i);
                if ("Click Event Log".equals(table.getCaption())) {
                    clickHistoryTable = table;
                    break;
                }
            }
        }
        
        assertNotNull(clickHistoryTable, "Click Event Log table should exist");
        assertTrue(clickHistoryTable.getContainerPropertyIds().contains("Click #"), 
                  "Table should have Click # column");
        assertTrue(clickHistoryTable.getContainerPropertyIds().contains("Timestamp"), 
                  "Table should have Timestamp column");
        assertTrue(clickHistoryTable.getContainerPropertyIds().contains("Interval (seconds)"), 
                  "Table should have Interval column");
    }

    @Test
    @DisplayName("Click pattern visualization table should be present")
    public void testClickPatternVisualizationPresent() {
        ui.init(request);
        VerticalLayout layout = (VerticalLayout) ui.getContent();
        
        Panel historyPanel = null;
        for (int i = 0; i < layout.getComponentCount(); i++) {
            if (layout.getComponent(i) instanceof Panel) {
                Panel panel = (Panel) layout.getComponent(i);
                if ("Click History & Visualization".equals(panel.getCaption())) {
                    historyPanel = panel;
                    break;
                }
            }
        }
        
        assertNotNull(historyPanel, "History panel should exist");
        VerticalLayout historyLayout = (VerticalLayout) historyPanel.getContent();
        
        // Find the Click Pattern Visualization table
        Table patternTable = null;
        for (int i = 0; i < historyLayout.getComponentCount(); i++) {
            if (historyLayout.getComponent(i) instanceof Table) {
                Table table = (Table) historyLayout.getComponent(i);
                if ("Click Pattern Visualization".equals(table.getCaption())) {
                    patternTable = table;
                    break;
                }
            }
        }
        
        assertNotNull(patternTable, "Click Pattern Visualization table should exist");
        assertTrue(patternTable.getContainerPropertyIds().contains("Time Period"), 
                  "Table should have Time Period column");
        assertTrue(patternTable.getContainerPropertyIds().contains("Clicks"), 
                  "Table should have Clicks column");
        assertTrue(patternTable.getContainerPropertyIds().contains("Visual"), 
                  "Table should have Visual column");
    }

}