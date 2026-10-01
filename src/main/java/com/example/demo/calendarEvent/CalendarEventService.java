package com.example.demo.calendarEvent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CalendarEventService {

    @Autowired
    private CalendarEventRepository repository;

    @Autowired
    private com.example.demo.users.UsersRepository usersRepository;

    // Codex 修改：驗證班次員工、時段及重疊；取消班次不占用時段。
    private void validateShift(CalendarEvent event, String existingId) {
        if (!"shift".equals(event.getCategory())) { event.setEmployeeId(null); return; }
        if (event.getEmployeeId() == null || event.getDate() == null || event.getStartTime() == null
                || event.getEndTime() == null || !event.getEndTime().isAfter(event.getStartTime())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "請選擇員工、日期，下班時間須晚於上班時間（同日班次）");
        }
        var employee = usersRepository.findById(event.getEmployeeId()).orElseThrow(() ->
                new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "找不到排班員工"));
        if (!"cancelled".equals(event.getStatus()) && repository.findByCategoryAndEmployeeIdAndDate("shift", event.getEmployeeId(), event.getDate())
                .stream().anyMatch(shift -> !java.util.Objects.equals(shift.getId(), existingId)
                        && !"cancelled".equals(shift.getStatus()) && shift.getStartTime().isBefore(event.getEndTime()) && shift.getEndTime().isAfter(event.getStartTime()))) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "此員工當天已有重疊的上班排班");
        }
        event.setTitle(employee.getName() + " · 上班排班");
        event.setAttendees(java.util.List.of(employee.getName()));
    }

    // 0. 取得所有排程列表
    public List<CalendarEvent> getAllEvents() {
        return repository.findAll();
    }

    // 1. 取得條件篩選後的排程清單
    public List<CalendarEvent> getFilteredEvents(CalendarQueryDTO query) {
        if (query == null) {
            return getAllEvents();
        }
        Specification<CalendarEvent> spec = CalendarEventSpecifications.filterEvents(query);
        return repository.findAll(spec);
    }

    // 2. 取得頂部四個統計圖卡的數據
    public CalendarStatsDTO getStats() {
        return new CalendarStatsDTO(
                repository.countTotal(),
                repository.countPending(),
                repository.countHighPriority(),
                repository.countCompleted());
    }

    // 3. 新增排程
    public CalendarEvent createEvent(CalendarEvent event) {
        event.setId(null); // 由資料庫/UUID 生成
        validateShift(event, null);
        return repository.save(event);
    }

    // 4. 更新排程
    public CalendarEvent updateEvent(String id, CalendarEvent updatedEvent) {
        validateShift(updatedEvent, id);
        return repository.findById(id).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setDescription(updatedEvent.getDescription());
            event.setCategory(updatedEvent.getCategory());
            event.setEmployeeId(updatedEvent.getEmployeeId());
            event.setDate(updatedEvent.getDate());
            event.setStartTime(updatedEvent.getStartTime());
            event.setEndTime(updatedEvent.getEndTime());
            event.setLocation(updatedEvent.getLocation());
            event.setOrganizer(updatedEvent.getOrganizer());
            event.setAttendees(
                    updatedEvent.getAttendees() != null ? updatedEvent.getAttendees() : new java.util.ArrayList<>());
            event.setPriority(updatedEvent.getPriority());
            event.setStatus(updatedEvent.getStatus());
            event.setRelatedRef(updatedEvent.getRelatedRef());
            event.setReminderMinutes(updatedEvent.getReminderMinutes());
            return repository.save(event);
        }).orElseThrow(() -> new RuntimeException("找不到該排程項目: " + id));
    }

    // 5. 切換/反轉排程狀態 (對應前端的 toggleEventStatus)
    public CalendarEvent toggleStatus(String id) {
        return repository.findById(id).map(event -> {
            if ("completed".equals(event.getStatus())) {
                event.setStatus("pending"); // 如果已完成，重開改為待處理
            } else {
                event.setStatus("completed"); // 否則標記為完成
            }
            // Codex 修改：恢復取消班次也需重新檢查時段重疊。
            validateShift(event, id);
            return repository.save(event);
        }).orElseThrow(() -> new RuntimeException("找不到該排程項目: " + id));
    }

    // 6. 刪除排程
    public void deleteEvent(String id) {
        repository.deleteById(id);
    }
}
