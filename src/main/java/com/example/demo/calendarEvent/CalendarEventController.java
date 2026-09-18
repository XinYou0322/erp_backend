package com.example.demo.calendarEvent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*") // 允許前端跨域呼欠，生產環境請改成特定網域
public class CalendarEventController {

    @Autowired
    private CalendarEventService service;

    // 查詢排程列表 (支援動態篩選)
    @PostMapping("/events/search")
    public ResponseEntity<List<CalendarEvent>> getEvents(@RequestBody CalendarQueryDTO query) {
        List<CalendarEvent> events = service.getFilteredEvents(query);
        return ResponseEntity.ok(events);
    }

    // 取得統計數據
    @GetMapping("/stats")
    public ResponseEntity<CalendarStatsDTO> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    // 建立新排程
    @PostMapping("/events")
    public ResponseEntity<CalendarEvent> createEvent(@RequestBody CalendarEvent event) {
        CalendarEvent created = service.createEvent(event);
        return ResponseEntity.ok(created);
    }

    // 修改排程
    @PutMapping("/events/{id}")
    public ResponseEntity<CalendarEvent> updateEvent(@PathVariable String id, @RequestBody CalendarEvent event) {
        CalendarEvent updated = service.updateEvent(id, event);
        return ResponseEntity.ok(updated);
    }

    // 快速切換狀態 (完成 / 重開)
    @PatchMapping("/events/{id}/toggle-status")
    public ResponseEntity<CalendarEvent> toggleStatus(@PathVariable String id) {
        CalendarEvent updated = service.toggleStatus(id);
        return ResponseEntity.ok(updated);
    }

    // 刪除排程
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        service.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
