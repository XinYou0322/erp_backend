package com.example.demo.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HourlySalesResponse {
    private Integer hour;
    private BigDecimal quantity;
}
