package com.example.demo.systemsetting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class SystemSettingServiceTest {

    private final SystemSettingRepository repository = mock(SystemSettingRepository.class);
    private final SystemSettingService service = new SystemSettingService(repository);

    @Test
    void missingReceivingSettingUsesSafeDefault() {
        assertFalse(service.isEnabled(SystemSettingKey.PURCHASE_ORDER_RECEIVING_ENABLED));
        assertEquals("false", service.get("PURCHASE_ORDER_RECEIVING_ENABLED").getValue());
    }

    @Test
    void adminUpdateNormalizesAndRecordsValue() {
        when(repository.findById("PURCHASE_ORDER_RECEIVING_ENABLED"))
                .thenReturn(Optional.empty());
        when(repository.save(any(SystemSetting.class))).thenAnswer(call -> call.getArgument(0));

        SystemSettingResponseDTO response = service.update(
                "PURCHASE_ORDER_RECEIVING_ENABLED", " TRUE ", 7L);

        assertEquals("true", response.getValue());
        assertEquals(7L, response.getUpdatedByUserId());
        verify(repository).save(any(SystemSetting.class));
    }

    @Test
    void booleanSettingRejectsUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> service.update(
                "PURCHASE_ORDER_RECEIVING_ENABLED", "yes", 7L));
    }
}
