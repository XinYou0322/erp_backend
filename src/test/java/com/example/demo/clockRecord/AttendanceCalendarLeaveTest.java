package com.example.demo.clockRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.example.demo.leave.*;
import com.example.demo.leave.enums.*;

// Codex 修改：驗證跨月、核准、待審、取消及部分時段與行事曆對應。
class AttendanceCalendarLeaveTest {
    @Test
    void calendarUsesActualLeaveRequests() {
        var service = new AttendanceCalendarService();
        var clocks = mock(ClockRecordRepository.class);
        var schedules = mock(WorkScheduleRepository.class);
        var holidays = mock(HolidayRepository.class);
        var leaves = mock(LeaveRequestRepository.class);
        ReflectionTestUtils.setField(service, "clockRecordRepository", clocks);
        ReflectionTestUtils.setField(service, "workScheduleRepository", schedules);
        ReflectionTestUtils.setField(service, "holidayRepository", holidays);
        ReflectionTestUtils.setField(service, "leaveRequestRepository", leaves);
        var start = LocalDate.of(2025, 9, 1);
        var approved = leave(1L, start.minusDays(1), start, LeaveStatus.APPROVED);
        var pending = leave(2L, start.plusDays(1), start.plusDays(1), LeaveStatus.PENDING);
        var cancelled = leave(3L, start.plusDays(2), start.plusDays(2), LeaveStatus.CANCELLED);
        var partial = leave(4L, start.plusDays(3), start.plusDays(3), LeaveStatus.APPROVED);
        partial.setLeaveDurationType(LeaveDurationType.PARTIAL_DAY);
        partial.setStartTime(LocalTime.of(9, 0));
        partial.setEndTime(LocalTime.of(12, 0));
        when(leaves.findByApplicantIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                7L, start.withDayOfMonth(30), start)).thenReturn(List.of(approved, pending, cancelled, partial));
        var days = service.getMonthlyAttendance("7", YearMonth.of(2025, 9));
        assertEquals(AttendanceStatus.LEAVE, days.get(0).getStatus());
        assertEquals(1L, days.get(0).getLeaves().get(0).id());
        assertEquals("SICK", days.get(0).getLeaves().get(0).leaveType());
        assertEquals(AttendanceStatus.ABSENT, days.get(1).getStatus());
        assertEquals("PENDING", days.get(1).getLeaves().get(0).status());
        assertTrue(days.get(2).getLeaves().isEmpty());
        assertNotEquals(AttendanceStatus.LEAVE, days.get(3).getStatus());
        assertEquals(LocalTime.of(9, 0), days.get(3).getLeaves().get(0).startTime());
        verify(leaves).findByApplicantIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(7L, start.withDayOfMonth(30), start);
    }

    private LeaveRequest leave(Long id, LocalDate start, LocalDate end, LeaveStatus status) {
        var leave = new LeaveRequest();
        leave.setId(id);
        leave.setStartDate(start);
        leave.setEndDate(end);
        leave.setStatus(status);
        leave.setLeaveType(LeaveType.SICK);
        return leave;
    }
}
