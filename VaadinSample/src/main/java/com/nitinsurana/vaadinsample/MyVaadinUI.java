package com.nitinsurana.vaadinsample;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Application's "main" class
 */
public class MyVaadinUI extends UI {

    private static final String STATS_PANEL_WIDTH = "400px";
    private static final String HISTORY_PANEL_WIDTH = "800px";
    
    private int clickCount = 0;
    private long sessionStartTime;
    private long lastClickTime;
    private Label totalClicksLabel;
    private Label sessionStartLabel;
    private Label timeSinceClickLabel;
    private Label sessionDurationLabel;
    private Label avgClicksPerMinuteLabel;
    private Label peakActivityLabel;
    private List<ClickEvent> clickHistory;
    private Table clickHistoryTable;
    private Table clickPatternTable;
    private Panel historyPanel;

    @Override
    protected void init(VaadinRequest request) {
        sessionStartTime = System.currentTimeMillis();
        lastClickTime = sessionStartTime;
        clickHistory = new ArrayList<>();
        
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        // Create statistics panel
        final Panel statsPanel = new Panel("Statistics Dashboard");
        statsPanel.setWidth(STATS_PANEL_WIDTH);
        VerticalLayout statsLayout = new VerticalLayout();
        statsLayout.setMargin(true);
        statsLayout.setSpacing(true);
        
        totalClicksLabel = new Label();
        sessionStartLabel = new Label();
        timeSinceClickLabel = new Label();
        sessionDurationLabel = new Label();
        avgClicksPerMinuteLabel = new Label();
        peakActivityLabel = new Label();
        
        updateStatistics();
        
        statsLayout.addComponent(totalClicksLabel);
        statsLayout.addComponent(sessionStartLabel);
        statsLayout.addComponent(timeSinceClickLabel);
        statsLayout.addComponent(sessionDurationLabel);
        statsLayout.addComponent(avgClicksPerMinuteLabel);
        statsLayout.addComponent(peakActivityLabel);
        statsPanel.setContent(statsLayout);
        layout.addComponent(statsPanel);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        final Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                long currentTime = System.currentTimeMillis();
                long interval = currentTime - lastClickTime;
                clickCount++;
                
                // Record click event
                clickHistory.add(new ClickEvent(new Date(currentTime), interval));
                
                updateStatistics();
                updateClickHistory();
                updateClickPattern();
                lastClickTime = currentTime;
                layout.addComponent(new Label("Thank you for clicking"));
            }
        });
        buttonLayout.addComponent(button);

        final Button resetButton = new Button("Clear Messages");
        resetButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                layout.removeAllComponents();
                layout.addComponent(statsPanel);
                layout.addComponent(buttonLayout);
                if (historyPanel != null) {
                    layout.addComponent(historyPanel);
                }
            }
        });
        buttonLayout.addComponent(resetButton);

        layout.addComponent(buttonLayout);
        
        // Create click history panel
        historyPanel = createClickHistoryPanel();
        layout.addComponent(historyPanel);
    }
    
    private void updateStatistics() {
        long currentTime = System.currentTimeMillis();
        long sessionDuration = (currentTime - sessionStartTime) / 1000;
        long timeSinceLastClick = (currentTime - lastClickTime) / 1000;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        totalClicksLabel.setValue("Total Clicks: " + clickCount);
        sessionStartLabel.setValue("Session Start Time: " + dateFormat.format(new Date(sessionStartTime)));
        timeSinceClickLabel.setValue("Time Since Last Click: " + timeSinceLastClick + " seconds");
        sessionDurationLabel.setValue("Session Duration: " + sessionDuration + " seconds");
        
        // Calculate average clicks per minute
        double avgClicksPerMinute = 0;
        if (sessionDuration > 0) {
            avgClicksPerMinute = (clickCount * 60.0) / sessionDuration;
        }
        avgClicksPerMinuteLabel.setValue(String.format("Average Clicks/Minute: %.2f", avgClicksPerMinute));
        
        // Calculate peak activity period
        String peakActivity = calculatePeakActivity();
        peakActivityLabel.setValue("Peak Activity: " + peakActivity);
    }
    
    private String calculatePeakActivity() {
        if (clickHistory.size() < 2) {
            return "N/A";
        }
        
        // Find the minute with the most clicks
        long startTime = sessionStartTime;
        long currentTime = System.currentTimeMillis();
        long totalMinutes = (currentTime - startTime) / 60000;
        
        if (totalMinutes < 1) {
            return "Current minute (" + clickHistory.size() + " clicks)";
        }
        
        int[] clicksPerMinute = new int[(int) totalMinutes + 1];
        for (ClickEvent event : clickHistory) {
            long minuteIndex = (event.getTimestamp().getTime() - startTime) / 60000;
            if (minuteIndex >= 0 && minuteIndex < clicksPerMinute.length) {
                clicksPerMinute[(int) minuteIndex]++;
            }
        }
        
        int maxClicks = 0;
        int peakMinute = 0;
        for (int i = 0; i < clicksPerMinute.length; i++) {
            if (clicksPerMinute[i] > maxClicks) {
                maxClicks = clicksPerMinute[i];
                peakMinute = i;
            }
        }
        
        if (maxClicks == 0) {
            return "N/A";
        }
        
        return "Minute " + peakMinute + " (" + maxClicks + " clicks)";
    }
    
    private Panel createClickHistoryPanel() {
        Panel panel = new Panel("Click History & Visualization");
        panel.setWidth(HISTORY_PANEL_WIDTH);
        
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        panelLayout.setSpacing(true);
        
        // Create click pattern visualization table
        clickPatternTable = new Table("Click Pattern Visualization");
        clickPatternTable.setWidth("100%");
        clickPatternTable.setPageLength(5);
        clickPatternTable.addContainerProperty("Time Period", String.class, null);
        clickPatternTable.addContainerProperty("Clicks", Integer.class, null);
        clickPatternTable.addContainerProperty("Visual", String.class, null);
        
        panelLayout.addComponent(clickPatternTable);
        
        // Create table for click history details
        clickHistoryTable = new Table("Click Event Log");
        clickHistoryTable.setWidth("100%");
        clickHistoryTable.setPageLength(10);
        clickHistoryTable.addContainerProperty("Click #", Integer.class, null);
        clickHistoryTable.addContainerProperty("Timestamp", String.class, null);
        clickHistoryTable.addContainerProperty("Interval (seconds)", String.class, null);
        
        panelLayout.addComponent(clickHistoryTable);
        
        panel.setContent(panelLayout);
        return panel;
    }
    
    private void updateClickHistory() {
        if (clickHistoryTable == null) {
            return;
        }
        
        clickHistoryTable.removeAllItems();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        
        for (int i = 0; i < clickHistory.size(); i++) {
            ClickEvent event = clickHistory.get(i);
            clickHistoryTable.addItem(new Object[]{
                i + 1,
                dateFormat.format(event.getTimestamp()),
                String.format("%.2f", event.getIntervalSinceLastClick() / 1000.0)
            }, i);
        }
    }
    
    private void updateClickPattern() {
        if (clickPatternTable == null) {
            return;
        }
        
        clickPatternTable.removeAllItems();
        
        // Group clicks by 10-second intervals
        long startTime = sessionStartTime;
        long currentTime = System.currentTimeMillis();
        long totalSeconds = (currentTime - startTime) / 1000;
        int intervals = (int) (totalSeconds / 10) + 1;
        
        int[] clicksPerInterval = new int[intervals];
        for (ClickEvent event : clickHistory) {
            long secondsFromStart = (event.getTimestamp().getTime() - startTime) / 1000;
            int intervalIndex = (int) (secondsFromStart / 10);
            if (intervalIndex >= 0 && intervalIndex < clicksPerInterval.length) {
                clicksPerInterval[intervalIndex]++;
            }
        }
        
        // Find max clicks for scaling the visualization
        int maxClicks = 1;
        for (int count : clicksPerInterval) {
            if (count > maxClicks) {
                maxClicks = count;
            }
        }
        
        // Add rows to table (show last 10 intervals)
        int startIdx = Math.max(0, intervals - 10);
        for (int i = startIdx; i < intervals; i++) {
            String timePeriod = String.format("%d-%d sec", i * 10, (i + 1) * 10);
            int clicks = clicksPerInterval[i];
            
            // Create visual bar
            int barLength = maxClicks > 0 ? (clicks * 20) / maxClicks : 0;
            StringBuilder visual = new StringBuilder();
            for (int j = 0; j < barLength; j++) {
                visual.append("█");
            }
            
            clickPatternTable.addItem(new Object[]{
                timePeriod,
                clicks,
                visual.toString()
            }, i);
        }
    }
}
