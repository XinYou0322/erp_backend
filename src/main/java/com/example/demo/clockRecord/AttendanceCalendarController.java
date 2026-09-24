package com.example.demo.clockRecord;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance")
public class AttendanceCalendarController {

    @Autowired
    private AttendanceCalendarService attendanceCalendarService;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * 取得某員工某個月份的每日出勤狀態，用於行事曆呈現
     * GET /api/attendance/calendar?userId=E001&yearMonth=2026-09
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> getMonthlyCalendar(
            @RequestParam String userId,
            @RequestParam String yearMonth) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "員工編號不能為空"));
        }

        YearMonth ym;
        try {
            ym = YearMonth.parse(yearMonth); // 格式需為 yyyy-MM
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "yearMonth 格式錯誤，需為 yyyy-MM"));
        }

        List<DailyAttendanceDto> days = attendanceCalendarService.getMonthlyAttendance(userId, ym);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "yearMonth", yearMonth,
                "days", days));
    }

    // ---------------- 排班設定管理 ----------------

    @GetMapping("/schedule")
    public ResponseEntity<?> listSchedules() {
        return ResponseEntity.ok(workScheduleRepository.findAll());
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> saveSchedule(@RequestBody WorkSchedule schedule) {
        return ResponseEntity.ok(workScheduleRepository.save(schedule));
    }

    @DeleteMapping("/schedule/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        workScheduleRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ---------------- 國定假日管理 ----------------

    @GetMapping("/holidays")
    public ResponseEntity<?> listHolidays(@RequestParam(required = false) String yearMonth) {
        if (yearMonth != null && !yearMonth.isBlank()) {
            YearMonth ym = YearMonth.parse(yearMonth);
            return ResponseEntity.ok(holidayRepository.findByDateBetween(ym.atDay(1), ym.atEndOfMonth()));
        }
        return ResponseEntity.ok(holidayRepository.findAll());
    }

    @PostMapping("/holidays")
    public ResponseEntity<?> addHoliday(@RequestBody Holiday holiday) {
        if (holidayRepository.existsByDate(holiday.getDate())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "該日期已存在假日設定"));
        }
        return ResponseEntity.ok(holidayRepository.save(holiday));
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<?> deleteHoliday(@PathVariable Long id) {
        holidayRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
