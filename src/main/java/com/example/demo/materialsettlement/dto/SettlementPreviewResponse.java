package com.example.demo.materialsettlement.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.materialsettlement.SettlementStatus;
import com.example.demo.materialsettlement.UnrecordedUsageReason;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettlementPreviewResponse {
    private Long settlementId;
    private LocalDate settlementDate;
    private SettlementStatus status;
    private String note;
    private Long createdByUserId;
    private String createdByName;
    private Instant completedAt;
    private boolean canComplete;
    private List<Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {
        private Long materialId;
        private String materialCode;
        private String materialName;
        private String unit;
        private BigDecimal previousCarryoverQuantity;
        private BigDecimal workspaceAvailableQuantity;
        private BigDecimal manualIssueQuantity;
        private BigDecimal theoreticalUsageQuantity;
        private BigDecimal wasteQuantity;
        private BigDecimal differenceQuantity;
        private BigDecimal unrecordedUsageQuantity;
        private UnrecordedUsageReason unrecordedReason;
        private String unrecordedNote;
        private BigDecimal workspaceCarryoverQuantity;
        private BigDecimal returnedQuantity;
        private LocalDate returnedExpiryDate;
        private BigDecimal unallocatedQuantity;
        private boolean returnExpiryRequired;
        private boolean valid;
        private String validationMessage;
    }
}
