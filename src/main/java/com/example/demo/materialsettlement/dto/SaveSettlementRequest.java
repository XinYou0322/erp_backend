package com.example.demo.materialsettlement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.materialsettlement.UnrecordedUsageReason;

import lombok.Data;

@Data
public class SaveSettlementRequest {
    private String note;
    private List<Item> items = new ArrayList<>();

    @Data
    public static class Item {
        private Long materialId;
        private BigDecimal unrecordedUsageQuantity;
        private UnrecordedUsageReason unrecordedReason;
        private String unrecordedNote;
        private BigDecimal workspaceCarryoverQuantity;
        private BigDecimal returnedQuantity;
        private LocalDate returnedExpiryDate;
    }
}
