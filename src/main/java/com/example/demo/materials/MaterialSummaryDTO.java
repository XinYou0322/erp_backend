package com.example.demo.materials;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaterialSummaryDTO {

    // 原物料總數
    private Long totalMaterials;

    // 平均成本
    private BigDecimal averageCost;

    // 有設定安全庫存的原物料數量
    private Long safetyStockCount;

    // 使用中的計量單位種類
    private Long unitCount;
}