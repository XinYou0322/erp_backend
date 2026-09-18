package com.example.demo.calendarEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarEventRepository
        extends JpaRepository<CalendarEvent, String>, JpaSpecificationExecutor<CalendarEvent> {

    // 計算全域或特定範圍統計數據
    @Query("SELECT COUNT(e) FROM CalendarEvent e")
    long countTotal();

    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.status = 'pending'")
    long countPending();

    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.priority = 'high' AND e.status != 'completed'")
    long countHighPriority();

    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.status = 'completed'")
    long countCompleted();
}
