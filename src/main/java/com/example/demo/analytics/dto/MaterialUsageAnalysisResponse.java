package com.example.demo.analytics.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaterialUsageAnalysisResponse {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String unit;
    private String analysisMode;
    private BigDecimal manualIssueQuantity;
    private BigDecimal autoDeductQuantity;
    private BigDecimal theoreticalUsageQuantity;
    private BigDecimal manualTheoreticalUsageQuantity;
    private BigDecimal autoTheoreticalUsageQuantity;
    private BigDecimal wasteQuantity;
    private BigDecimal expiredQuantity;
    private BigDecimal manualVarianceQuantity;
    private BigDecimal autoVarianceQuantity;
    private BigDecimal combinedVarianceQuantity;
    private BigDecimal varianceRate;
    private String riskLevel;
    private BigDecimal accountedUsageQuantity;
    private BigDecimal estimatedWorkspaceRemaining;
    private String recommendation;
}
