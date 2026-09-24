package com.example.demo.systemsetting;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemSettingUpdateDTO {
    @NotBlank(message = "設定值不得為空")
    private String value;
}
