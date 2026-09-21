package com.example.demo.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueTrendResponse {

    private LocalDate date;
    private BigDecimal revenue;
}
