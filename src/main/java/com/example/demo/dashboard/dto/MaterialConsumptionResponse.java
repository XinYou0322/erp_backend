package com.example.demo.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialConsumptionResponse {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String unit;
    private BigDecimal manualIssueQuantity;
    private BigDecimal theoreticalUsageQuantity;
    private BigDecimal varianceQuantity;
}
