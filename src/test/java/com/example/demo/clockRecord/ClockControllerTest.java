package com.example.demo.clockRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.example.demo.users.*;

// Codex 修改：驗證今日查詢、重複阻擋、先上班後下班及登入身分。
class ClockControllerTest {
    @Test
    void validatesDailySequenceUsingAuthenticatedUser() {
        var controller = new ClockController();
        var records = mock(ClockRecordRepository.class);
        var users = mock(UsersRepository.class);
        var user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        when(users.findForClockUpdate("alice")).thenReturn(Optional.of(user));
        ReflectionTestUtils.setField(controller, "clockRecordRepository", records);
        ReflectionTestUtils.setField(controller, "usersRepository", users);
        var in = new ClockRecord("7", LocalDateTime.now(), "CLOCK_IN");
        var out = new ClockRecord("7", LocalDateTime.now(), "CLOCK_OUT");
        when(records.findByUserIdAndClockTimeGreaterThanEqualAndClockTimeLessThan(eq("7"), any(), any()))
                .thenReturn(List.of(), List.of(), List.of(in), List.of(in), List.of(in, out));
        assertEquals(409, controller.toggleClock(Map.of("currentStatus", "IN"), () -> "alice").getStatusCode().value());
        assertEquals(200, controller.toggleClock(Map.of("currentStatus", "OUT", "userId", "999"), () -> "alice").getStatusCode().value());
        assertEquals(409, controller.toggleClock(Map.of("currentStatus", "OUT"), () -> "alice").getStatusCode().value());
        assertEquals(200, controller.toggleClock(Map.of("currentStatus", "IN"), () -> "alice").getStatusCode().value());
        assertEquals(409, controller.toggleClock(Map.of("currentStatus", "OUT"), () -> "alice").getStatusCode().value());
        verify(records, times(2)).save(argThat(record -> "7".equals(record.getUserId())));
    }
}
