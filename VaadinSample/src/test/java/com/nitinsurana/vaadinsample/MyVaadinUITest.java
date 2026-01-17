package com.nitinsurana.vaadinsample;

import com.vaadin.server.FileDownloader;
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
    
    /**
     * Helper method to find the button layout from the main layout.
     */
    private com.vaadin.ui.HorizontalLayout findButtonLayout(VerticalLayout mainLayout) {
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                return (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
            }
        }
        return null;
    }
    
    /**
     * Helper method to find a button by its caption in the button layout.
     */
    private Button findButtonByCaption(com.vaadin.ui.HorizontalLayout buttonLayout, String caption) {
        for (int i = 0; i < buttonLayout.getComponentCount(); i++) {
            if (buttonLayout.getComponent(i) instanceof Button) {
                Button btn = (Button) buttonLayout.getComponent(i);
                if (caption.equals(btn.getCaption())) {
                    return btn;
                }
            }
        }
        return null;
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
    @DisplayName("Button click should increment click counter and add history entry")
    public void testButtonClickIncrementsCounter() {
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
        
        // Get history panel and verify initial state
        Panel historyPanel = (Panel) mainLayout.getComponent(1);
        VerticalLayout historyLayout = (VerticalLayout) historyPanel.getContent();
        assertEquals(0, historyLayout.getComponentCount(), "History should be empty initially");
        
        // Simulate button click
        clickButton.click();
        
        // Verify history entry was added
        assertEquals(1, historyLayout.getComponentCount(), "History should have 1 entry after click");
        Label historyEntry = (Label) historyLayout.getComponent(0);
        assertTrue(historyEntry.getValue().startsWith("Click #1 at "), 
                  "History entry should have correct format");
    }

    @Test
    @DisplayName("Multiple clicks should add multiple history entries")
    public void testMultipleClicksAddMultipleEntries() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Find the Click Me button
        com.vaadin.ui.HorizontalLayout buttonLayout = null;
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                buttonLayout = (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
                break;
            }
        }
        
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
        
        // Get history panel
        Panel historyPanel = (Panel) mainLayout.getComponent(1);
        VerticalLayout historyLayout = (VerticalLayout) historyPanel.getContent();
        
        // Click multiple times
        clickButton.click();
        clickButton.click();
        clickButton.click();
        
        // Verify 3 history entries were added
        assertEquals(3, historyLayout.getComponentCount(), "History should have 3 entries after 3 clicks");
        
        // Verify entries have correct format
        Label firstEntry = (Label) historyLayout.getComponent(0);
        Label secondEntry = (Label) historyLayout.getComponent(1);
        Label thirdEntry = (Label) historyLayout.getComponent(2);
        
        assertTrue(firstEntry.getValue().startsWith("Click #1 at "), "First entry should be Click #1");
        assertTrue(secondEntry.getValue().startsWith("Click #2 at "), "Second entry should be Click #2");
        assertTrue(thirdEntry.getValue().startsWith("Click #3 at "), "Third entry should be Click #3");
    }

    @Test
    @DisplayName("Clear History button should remove all history entries")
    public void testClearHistoryButtonFunctionality() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Find buttons
        com.vaadin.ui.HorizontalLayout buttonLayout = null;
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                buttonLayout = (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
                break;
            }
        }
        
        assertNotNull(buttonLayout, "Button layout with action buttons should exist");
        
        Button clickButton = null;
        Button clearHistoryButton = null;
        for (int i = 0; i < buttonLayout.getComponentCount(); i++) {
            if (buttonLayout.getComponent(i) instanceof Button) {
                Button btn = (Button) buttonLayout.getComponent(i);
                if ("Click Me".equals(btn.getCaption())) {
                    clickButton = btn;
                } else if ("Clear History".equals(btn.getCaption())) {
                    clearHistoryButton = btn;
                }
            }
        }
        
        assertNotNull(clickButton, "Click Me button should exist");
        assertNotNull(clearHistoryButton, "Clear History button should exist");
        
        // Get history panel
        Panel historyPanel = (Panel) mainLayout.getComponent(1);
        VerticalLayout historyLayout = (VerticalLayout) historyPanel.getContent();
        
        // Add some history entries
        clickButton.click();
        clickButton.click();
        assertEquals(2, historyLayout.getComponentCount(), "History should have 2 entries");
        
        // Clear history
        clearHistoryButton.click();
        
        // Verify history is cleared
        assertEquals(0, historyLayout.getComponentCount(), "History should be empty after clearing");
    }

    @Test
    @DisplayName("Time since last click should be calculated correctly")
    public void testTimeSinceLastClickCalculation() throws InterruptedException {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        
        // Find the Click Me button
        com.vaadin.ui.HorizontalLayout buttonLayout = null;
        for (int i = 0; i < mainLayout.getComponentCount(); i++) {
            if (mainLayout.getComponent(i) instanceof com.vaadin.ui.HorizontalLayout) {
                buttonLayout = (com.vaadin.ui.HorizontalLayout) mainLayout.getComponent(i);
                break;
            }
        }
        
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
        
        // Get stats panel
        Panel statsPanel = (Panel) mainLayout.getComponent(0);
        VerticalLayout statsLayout = (VerticalLayout) statsPanel.getContent();
        Label timeSinceClickLabel = (Label) statsLayout.getComponent(2);
        
        // First click - time since last click should be 0 or very small (initial state)
        clickButton.click();
        String firstValue = timeSinceClickLabel.getValue();
        assertTrue(firstValue.contains("Time Since Last Click:"), "Label should show time since last click");
        
        // Wait a bit (at least 1 second to ensure measurable difference)
        Thread.sleep(1100);
        
        // Second click - time since last click should be at least 1 second (not 0)
        clickButton.click();
        String secondValue = timeSinceClickLabel.getValue();
        
        // Extract the number of seconds from the label using regex for robust parsing
        // Expected format: "Time Since Last Click: X seconds"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Time Since Last Click:\\s*(\\d+)\\s*seconds?");
        java.util.regex.Matcher matcher = pattern.matcher(secondValue);
        
        assertTrue(matcher.find(), "Label should match expected format: '" + secondValue + "'");
        
        int seconds = Integer.parseInt(matcher.group(1));
        assertTrue(seconds >= 1, "Time since last click should be at least 1 second, but was: " + seconds);
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
        // Expected dimensions match HISTORY_PANEL_WIDTH and HISTORY_PANEL_HEIGHT constants
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
    @DisplayName("Panels should be properly initialized")
    public void testPanelsInitialized() {
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
        
        assertEquals(3, buttonCount, "Should have exactly 3 buttons (Click Me, Clear History, and Export CSV)");
    }

    @Test
    @DisplayName("Export CSV button should be present")
    public void testExportCsvButtonPresent() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        com.vaadin.ui.HorizontalLayout buttonLayout = findButtonLayout(mainLayout);
        assertNotNull(buttonLayout, "Button layout should exist");
        
        Button exportCsvButton = findButtonByCaption(buttonLayout, "Export CSV");
        assertNotNull(exportCsvButton, "Export CSV button should exist");
    }

    @Test
    @DisplayName("Export CSV button should have FileDownloader extension")
    public void testExportCsvButtonHasFileDownloaderExtension() {
        ui.init(request);
        
        VerticalLayout mainLayout = (VerticalLayout) ui.getContent();
        com.vaadin.ui.HorizontalLayout buttonLayout = findButtonLayout(mainLayout);
        assertNotNull(buttonLayout, "Button layout should exist");
        
        Button exportCsvButton = findButtonByCaption(buttonLayout, "Export CSV");
        assertNotNull(exportCsvButton, "Export CSV button should exist");
        
        // Check that the button has a FileDownloader extension
        boolean hasFileDownloader = false;
        for (Object extension : exportCsvButton.getExtensions()) {
            if (extension instanceof FileDownloader) {
                hasFileDownloader = true;
                break;
            }
        }
        assertTrue(hasFileDownloader, "Export CSV button should have FileDownloader extension");
    }

}