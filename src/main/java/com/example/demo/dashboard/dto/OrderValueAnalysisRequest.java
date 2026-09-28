package com.example.demo.dashboard.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrderValueAnalysisRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
