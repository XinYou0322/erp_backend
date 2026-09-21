package com.example.demo.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductResponse {

    private String productName;
    private BigDecimal quantity;
}
