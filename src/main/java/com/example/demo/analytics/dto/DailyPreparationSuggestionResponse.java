package com.example.demo.analytics.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyPreparationSuggestionResponse {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String unit;
    private BigDecimal averageUsage7Days;
    private BigDecimal averageUsage30Days;
    private BigDecimal estimatedTodayUsage;
    private BigDecimal previousCarryoverQuantity;
    private BigDecimal bufferQuantity;
    private BigDecimal rawSuggestedIssueQuantity;
    private BigDecimal issueStep;
    private BigDecimal suggestedIssueQuantity;
    private BigDecimal availableInventoryQuantity;
    private BigDecimal executableIssueQuantity;
    private BigDecimal shortageQuantity;
    private String status;
    private String recommendation;
}
