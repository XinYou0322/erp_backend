package com.example.demo.analytics.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplenishmentSuggestionResponse {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String unit;
    private BigDecimal availableQuantity;
    private BigDecimal expiredQuantity;
    private BigDecimal safetyStock;
    private BigDecimal recentUsageQuantity;
    private BigDecimal averageDailyUsage;
    private BigDecimal forecastUsageQuantity;
    private BigDecimal pendingPurchaseQuantity;
    private BigDecimal suggestedPurchaseQuantity;
    private BigDecimal estimatedDaysRemaining;
    private String inventoryStatus;
    private String riskLevel;
}
