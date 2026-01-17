package com.nitinsurana.vaadinsample;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    
    // ThreadLocal for filename date format (YYYYMMDD_HHMM)
    private static final ThreadLocal<SimpleDateFormat> FILENAME_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd_HHmm");
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
    private List<Long> clickTimestamps;

    @Override
    protected void init(VaadinRequest request) {
        sessionStartTime = System.currentTimeMillis();
        lastClickTime = sessionStartTime;
        clickHistory = new ArrayList<>();
        clickTimestamps = new ArrayList<>();
        
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
                updateStatistics();
                lastClickTime = System.currentTimeMillis();
                addClickToHistory();
            }
        });
        buttonLayout.addComponent(button);

        final Button clearHistoryButton = new Button("Clear History");
        clearHistoryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                clickHistory.clear();
                clickTimestamps.clear();
                historyLayout.removeAllComponents();
            }
        });
        buttonLayout.addComponent(clearHistoryButton);

        // Export CSV button with FileDownloader
        final Button exportCsvButton = new Button("Export CSV");
        final FileDownloader fileDownloader = new FileDownloader(createCsvResource());
        fileDownloader.extend(exportCsvButton);
        // Update resource before each download to reflect current time and data
        exportCsvButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                StreamResource freshCsvResource = createCsvResource();
                freshCsvResource.setFilename(generateCsvFilename());
                fileDownloader.setFileDownloadResource(freshCsvResource);
            }
        });
        buttonLayout.addComponent(exportCsvButton);

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
        clickTimestamps.add(lastClickTime);
        
        Label historyLabel = new Label(historyEntry);
        historyLayout.addComponent(historyLabel);
    }
    
    private StreamResource createCsvResource() {
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                StringBuilder csv = new StringBuilder();
                csv.append("Click Number,Timestamp\n");
                
                // Take a defensive copy to avoid concurrent modification while iterating
                List<Long> timestampsSnapshot;
                synchronized (clickTimestamps) {
                    timestampsSnapshot = new ArrayList<>(clickTimestamps);
                }

                for (int i = 0; i < timestampsSnapshot.size(); i++) {
                    int clickNumber = i + 1;
                    String timestamp = DATE_FORMAT.get().format(new Date(timestampsSnapshot.get(i)));
                    csv.append(clickNumber)
                       .append(",")
                       .append("\"")
                       .append(timestamp.replace("\"", "\"\""))
                       .append("\"")
                       .append("\n");
                }
                
                return new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
            }
        }, generateCsvFilename());
        resource.setMIMEType("text/csv");
        return resource;
    }
    
    private String generateCsvFilename() {
        String dateTime = FILENAME_DATE_FORMAT.get().format(new Date());
        return "click_history_" + dateTime + ".csv";
    }
}
