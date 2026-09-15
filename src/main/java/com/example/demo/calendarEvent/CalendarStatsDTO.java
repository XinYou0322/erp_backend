package com.example.demo.calendarEvent;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalendarStatsDTO {

    private long totalEvents;
    private long pendingEventsCount;
    private long highPriorityCount;
    private long completedEventsCount;
}
