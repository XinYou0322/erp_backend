package com.example.demo.systemsetting;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemSettingResponseDTO {
    private String key;
    private String value;
    private String description;
    private LocalDateTime updatedAt;
    private Long updatedByUserId;
}
