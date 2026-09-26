package com.example.demo.systemsetting;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SystemSettingUpdateDTO {
    @NotNull(message = "設定值不得為 null")
    private String value;
}
