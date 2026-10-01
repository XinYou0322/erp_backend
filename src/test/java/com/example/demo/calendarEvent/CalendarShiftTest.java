package com.example.demo.calendarEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.users.*;

// Codex 修改：驗證員工班次、相鄰時段與重疊阻擋。
class CalendarShiftTest {
    @Test
    void rejectsOverlapAndAllowsAdjacentShift() {
        var service = new CalendarEventService();
        var repository = mock(CalendarEventRepository.class);
        var users = mock(UsersRepository.class);
        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(service, "usersRepository", users);
        var employee = new User();
        employee.setName("測試員工");
        when(users.findById(7L)).thenReturn(Optional.of(employee));
        var existing = shift(9, 12);
        existing.setId("existing");
        when(repository.findByCategoryAndEmployeeIdAndDate("shift", 7L, existing.getDate())).thenReturn(List.of(existing));
        assertThrows(ResponseStatusException.class, () -> service.createEvent(shift(11, 13)));
        verify(repository, never()).save(any());
        var adjacent = shift(12, 18);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var saved = service.createEvent(adjacent);
        assertEquals(7L, saved.getEmployeeId());
        assertEquals("測試員工 · 上班排班", saved.getTitle());
        assertEquals(List.of("測試員工"), saved.getAttendees());
        assertThrows(ResponseStatusException.class, () -> service.createEvent(shift(18, 9)));
    }
    private CalendarEvent shift(int start, int end) {
        var event = new CalendarEvent();
        event.setCategory("shift");
        event.setEmployeeId(7L);
        event.setDate(LocalDate.of(2026, 10, 1));
        event.setStartTime(LocalTime.of(start, 0));
        event.setEndTime(LocalTime.of(end, 0));
        event.setStatus("pending");
        return event;
    }
}
