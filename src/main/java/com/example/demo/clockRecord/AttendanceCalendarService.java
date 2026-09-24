package com.example.demo.clockRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendanceCalendarService {

    @Autowired
    private ClockRecordRepository clockRecordRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    // 找不到任何排班設定時的保底預設值 (週一到週五 09:00-18:00，無緩衝)
    private static final WorkSchedule FALLBACK_SCHEDULE =
            new WorkSchedule(null, LocalTime.of(9, 0), LocalTime.of(18, 0));

    public List<DailyAttendanceDto> getMonthlyAttendance(String userId, YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        LocalDate today = LocalDate.now();

        WorkSchedule schedule = resolveSchedule(userId);

        Set<LocalDate> holidaySet = holidayRepository.findByDateBetween(start, end)
                .stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        // 該月份此員工的所有打卡紀錄，先撈出來再依日期分組，避免逐日查詢資料庫
        List<ClockRecord> monthRecords = clockRecordRepository.findByUserIdOrderByClockTimeDesc(userId)
                .stream()
                .filter(r -> {
                    LocalDate d = r.getClockTime().toLocalDate();
                    return !d.isBefore(start) && !d.isAfter(end);
                })
                .collect(Collectors.toList());

        List<DailyAttendanceDto> result = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // lambda 內只能使用 effectively final 的變數，for 迴圈的 date 每次疊代都會被重新賦值，
            // 所以要在迴圈內另外宣告一個不會再變動的區域變數給 stream 使用
            final LocalDate current = date;
            List<ClockRecord> dayRecords = monthRecords.stream()
                    .filter(r -> r.getClockTime().toLocalDate().equals(current))
                    .sorted(Comparator.comparing(ClockRecord::getClockTime))
                    .collect(Collectors.toList());

            DailyAttendanceDto dto = buildDto(current, dayRecords, schedule, holidaySet, today);
            result.add(dto);
        }

        return result;
    }

    private WorkSchedule resolveSchedule(String userId) {
        if (userId != null && !userId.isBlank()) {
            var byUser = workScheduleRepository.findByUserId(userId);
            if (byUser.isPresent()) {
                return byUser.get();
            }
        }
        return workScheduleRepository.findByUserIdIsNull().orElse(FALLBACK_SCHEDULE);
    }

    private boolean isWorkday(LocalDate date, WorkSchedule schedule, Set<LocalDate> holidays) {
        if (holidays.contains(date)) {
            return false;
        }
        int iso = date.getDayOfWeek().getValue(); // 1=一 ... 7=日
        for (String part : schedule.getWorkdays().split(",")) {
            if (String.valueOf(iso).equals(part.trim())) {
                return true;
            }
        }
        return false;
    }

    private DailyAttendanceDto buildDto(LocalDate date, List<ClockRecord> dayRecords, WorkSchedule schedule,
            Set<LocalDate> holidaySet, LocalDate today) {

        DailyAttendanceDto dto = new DailyAttendanceDto();
        dto.setDate(date.toString());
        dto.setRecords(toRecordMaps(dayRecords));

        LocalDateTime firstClockIn = dayRecords.stream()
                .filter(r -> "CLOCK_IN".equals(r.getClockType()))
                .map(ClockRecord::getClockTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime lastClockOut = dayRecords.stream()
                .filter(r -> "CLOCK_OUT".equals(r.getClockType()))
                .map(ClockRecord::getClockTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        dto.setClockInTime(firstClockIn != null ? firstClockIn.toString() : null);
        dto.setClockOutTime(lastClockOut != null ? lastClockOut.toString() : null);

        boolean workday = isWorkday(date, schedule, holidaySet);

        if (!workday) {
            dto.setStatus(AttendanceStatus.REST_DAY);
        } else if (date.isAfter(today)) {
            dto.setStatus(AttendanceStatus.FUTURE);
        } else {
            dto.setStatus(computeStatus(date, firstClockIn, lastClockOut, schedule, date.equals(today)));
        }

        return dto;
    }

    private AttendanceStatus computeStatus(LocalDate date, LocalDateTime firstClockIn, LocalDateTime lastClockOut,
            WorkSchedule schedule, boolean isToday) {

        if (firstClockIn == null && lastClockOut == null) {
            return AttendanceStatus.ABSENT;
        }

        // 今天已打卡上班但還沒打卡下班 -> 上班中，先不判定遲到/早退以外的結論
        if (lastClockOut == null && isToday) {
            boolean lateSoFar = firstClockIn != null
                    && firstClockIn.isAfter(date.atTime(schedule.getStartTime()).plusMinutes(schedule.getLateGraceMinutes()));
            return lateSoFar ? AttendanceStatus.LATE : AttendanceStatus.INCOMPLETE;
        }

        LocalDateTime scheduledStart = date.atTime(schedule.getStartTime()).plusMinutes(schedule.getLateGraceMinutes());
        LocalDateTime scheduledEnd = date.atTime(schedule.getEndTime()).minusMinutes(schedule.getEarlyLeaveGraceMinutes());

        boolean isLate = firstClockIn != null && firstClockIn.isAfter(scheduledStart);
        boolean isEarlyLeave = lastClockOut != null && lastClockOut.isBefore(scheduledEnd);

        if (isLate && isEarlyLeave) {
            return AttendanceStatus.LATE_AND_EARLY_LEAVE;
        }
        if (isLate) {
            return AttendanceStatus.LATE;
        }
        if (isEarlyLeave) {
            return AttendanceStatus.EARLY_LEAVE;
        }
        return AttendanceStatus.NORMAL;
    }

    private List<Map<String, Object>> toRecordMaps(List<ClockRecord> records) {
        return records.stream().map(r -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("userId", r.getUserId());
            item.put("clockType", r.getClockType());
            item.put("clockTime", r.getClockTime().toString());
            item.put("label", "CLOCK_IN".equals(r.getClockType()) ? "打卡上班" : "打卡下班");
            return item;
        }).collect(Collectors.toList());
    }
}
