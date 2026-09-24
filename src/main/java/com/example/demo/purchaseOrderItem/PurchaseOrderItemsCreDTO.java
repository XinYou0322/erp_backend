package com.example.demo.purchaseOrderItem;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseOrderItemsCreDTO {
	
	
	// 前端選擇的原物料 ID
    @NotNull(message = "原物料 ID 不可為空")
    @Positive(message = "原物料 ID 必須大於 0")
    private Long materialId;

    // 採購數量
    @NotNull(message = "採購數量不可為空")
    @DecimalMin(value = "0.0001", message = "採購數量必須大於 0")
    private BigDecimal quantity;

    // 採購成交單價
    @NotNull(message = "採購單價不可為空")
    @DecimalMin(value = "0.00", inclusive = false, message = "採購單價必須大於 0")
    private BigDecimal price;
}
