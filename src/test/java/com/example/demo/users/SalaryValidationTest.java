package com.example.demo.users;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

// Codex 修改：新增與編輯薪資都必須阻擋負數、溢位及多餘小數。
class SalaryValidationTest {
    @Test
    void validatesSalaryForBothForms() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            for (var type : new Class<?>[] {UserRegisterDTO.class, UserUpdateDTO.class}) {
                for (String value : new String[] {"-1", "100000000", "36000.001"}) {
                    assertFalse(validator.validateValue(type, "salary", new BigDecimal(value)).isEmpty());
                }
                for (String value : new String[] {"0", "36000", "36000.50", "99999999.99"}) {
                    assertTrue(validator.validateValue(type, "salary", new BigDecimal(value)).isEmpty());
                }
                assertTrue(validator.validateValue(type, "salary", null).isEmpty());
            }
        }
    }
}
