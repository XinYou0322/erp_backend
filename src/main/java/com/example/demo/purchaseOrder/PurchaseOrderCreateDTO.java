package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PurchaseOrderCreateDTO {

    // 供應商 ID
    @NotNull(message = "供應商不可為空")
    private Long supplierId;


    // 建立人 User ID
    @NotNull(message = "建立人不可為空")
    private Long createdByUserId;


    // 簽核人 User ID
    @NotNull(message = "審核人不可為空")
    private Long approvedByUserId;


    // 預計到貨日期
    @NotNull(message = "到貨日期不可為空")
    private LocalDate expectedDeliveryDate;


    // 採購總金額
    @NotNull(message = "總金額不可為空")
    @PositiveOrZero(message = "總金額不可小於 0")
    private BigDecimal total;
}