package com.example.demo.calendarEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "calendar_events")
@Data
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // 符合前端使用字串型態的 ID
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category; // procurement, production, meeting, leave, maintenance, marketing

    @Column(nullable = false)
    private LocalDate date; // yyyy-MM-dd

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // HH:mm

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // HH:mm

    private String location;
    private String organizer;

    @ElementCollection // 處理前端傳入的成員陣列 (JSON 陣列字串或關聯表)
    @CollectionTable(name = "event_attendees", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "attendee_name")
    private List<String> attendees;

    @Column(nullable = false)
    private String priority; // high, medium, low

    @Column(nullable = false)
    private String status; // pending, in_progress, completed, cancelled

    @Column(name = "related_ref")
    private String relatedRef; // 關聯單據單號

    @Column(name = "reminder_minutes")
    private Integer reminderMinutes = 15;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
