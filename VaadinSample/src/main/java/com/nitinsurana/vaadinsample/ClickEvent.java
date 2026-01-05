package com.nitinsurana.vaadinsample;

import java.util.Date;

/**
 * Represents a single click event with timestamp
 */
public class ClickEvent {
    private final Date timestamp;
    private final long intervalSinceLastClick;

    public ClickEvent(Date timestamp, long intervalSinceLastClick) {
        this.timestamp = timestamp;
        this.intervalSinceLastClick = intervalSinceLastClick;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public long getIntervalSinceLastClick() {
        return intervalSinceLastClick;
    }
}
