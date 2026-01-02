package com.nitinsurana.vaadinsample;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.util.Date;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    private int clickCount = 0;
    private long sessionStartTime;
    private long lastClickTime;
    private Label statsLabel;

    @Override
    protected void init(VaadinRequest request) {
        sessionStartTime = System.currentTimeMillis();
        lastClickTime = sessionStartTime;
        
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        // Create statistics panel
        final Panel statsPanel = new Panel("Statistics Dashboard");
        statsPanel.setWidth("400px");
        VerticalLayout statsLayout = new VerticalLayout();
        statsLayout.setMargin(true);
        statsLayout.setSpacing(true);
        
        statsLabel = new Label();
        statsLabel.setContentMode(Label.CONTENT_XHTML);
        updateStatistics();
        
        statsLayout.addComponent(statsLabel);
        statsPanel.setContent(statsLayout);
        layout.addComponent(statsPanel);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        final Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                clickCount++;
                updateStatistics();
                lastClickTime = System.currentTimeMillis();
                layout.addComponent(new Label("Thank you for clicking"));
            }
        });
        buttonLayout.addComponent(button);

        final Button resetButton = new Button("Clear Messages");
        resetButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                layout.removeAllComponents();
                layout.addComponent(statsPanel);
                layout.addComponent(buttonLayout);
            }
        });
        buttonLayout.addComponent(resetButton);

        layout.addComponent(buttonLayout);
    }
    
    private void updateStatistics() {
        long currentTime = System.currentTimeMillis();
        long sessionDuration = (currentTime - sessionStartTime) / 1000;
        long timeSinceLastClick = (currentTime - lastClickTime) / 1000;
        
        String stats = "<div style='font-family: Arial, sans-serif;'>" +
                "<div style='margin-bottom: 10px;'><strong>Total Clicks:</strong> <span style='color: #2196F3; font-size: 18px;'>" + clickCount + "</span></div>" +
                "<div style='margin-bottom: 10px;'><strong>Session Start Time:</strong> " + new Date(sessionStartTime) + "</div>" +
                "<div style='margin-bottom: 10px;'><strong>Time Since Last Click:</strong> " + timeSinceLastClick + " seconds</div>" +
                "<div style='margin-bottom: 10px;'><strong>Session Duration:</strong> " + sessionDuration + " seconds</div>" +
                "</div>";
        
        statsLabel.setValue(stats);
    }
}
