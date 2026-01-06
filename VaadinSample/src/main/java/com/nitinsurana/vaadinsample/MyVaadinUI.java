package com.nitinsurana.vaadinsample;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
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
    private static final String HISTORY_PANEL_WIDTH = "500px";
    private static final String HISTORY_PANEL_HEIGHT = "300px";
    
    // ThreadLocal to ensure thread-safety for SimpleDateFormat in multi-user web application
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    
    private int clickCount = 0;
    private long sessionStartTime;
    private long lastClickTime;
    private Label totalClicksLabel;
    private Label sessionStartLabel;
    private Label timeSinceClickLabel;
    private Label sessionDurationLabel;
    private VerticalLayout historyLayout;
    private List<String> clickHistory;

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
        
        updateStatistics();
        
        statsLayout.addComponent(totalClicksLabel);
        statsLayout.addComponent(sessionStartLabel);
        statsLayout.addComponent(timeSinceClickLabel);
        statsLayout.addComponent(sessionDurationLabel);
        statsPanel.setContent(statsLayout);
        layout.addComponent(statsPanel);

        // Create click history panel
        final Panel historyPanel = new Panel("Click History Log");
        historyPanel.setWidth(HISTORY_PANEL_WIDTH);
        historyPanel.setHeight(HISTORY_PANEL_HEIGHT);
        historyLayout = new VerticalLayout();
        historyLayout.setMargin(true);
        historyLayout.setSpacing(true);
        historyPanel.setContent(historyLayout);
        layout.addComponent(historyPanel);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        final Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                clickCount++;
                lastClickTime = System.currentTimeMillis();
                updateStatistics();
                addClickToHistory();
            }
        });
        buttonLayout.addComponent(button);

        final Button clearHistoryButton = new Button("Clear History");
        clearHistoryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                clickHistory.clear();
                historyLayout.removeAllComponents();
            }
        });
        buttonLayout.addComponent(clearHistoryButton);

        layout.addComponent(buttonLayout);
    }
    
    private void updateStatistics() {
        long currentTime = System.currentTimeMillis();
        long sessionDuration = (currentTime - sessionStartTime) / 1000;
        long timeSinceLastClick = (currentTime - lastClickTime) / 1000;
        
        totalClicksLabel.setValue("Total Clicks: " + clickCount);
        sessionStartLabel.setValue("Session Start Time: " + DATE_FORMAT.get().format(new Date(sessionStartTime)));
        timeSinceClickLabel.setValue("Time Since Last Click: " + timeSinceLastClick + " seconds");
        sessionDurationLabel.setValue("Session Duration: " + sessionDuration + " seconds");
    }
    
    private void addClickToHistory() {
        String timestamp = DATE_FORMAT.get().format(new Date(lastClickTime));
        String historyEntry = "Click #" + clickCount + " at " + timestamp;
        clickHistory.add(historyEntry);
        
        Label historyLabel = new Label(historyEntry);
        historyLayout.addComponent(historyLabel);
    }
}
