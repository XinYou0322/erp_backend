package com.example.demo.inventories;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventorySummaryDTO {

    private Long materialId;

    private String code;

    private String name;

    private String unit;

    // 所有批次加總後的庫存
    private BigDecimal totalQuantity;

    // 原物料成本
    private BigDecimal cost;

    
    private BigDecimal safetyStock;
    private String status;
    
    
    // 最近到期日
    private LocalDate nearestExpiryDate;

}