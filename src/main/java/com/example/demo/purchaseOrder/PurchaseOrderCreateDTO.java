package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.purchaseOrderItem.PurchaseOrderItemsCreDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PurchaseOrderCreateDTO {

    // 供應商 ID
    @NotNull(message = "供應商不可為空")
    @Positive(message = "供應商 ID 必須大於 0")
    private Long supplierId;

    // 簽核人 User ID
    @NotNull(message = "審核人不可為空")
    @Positive(message = "審核人 ID 必須大於 0")
    private Long approvedByUserId;

    // 預計到貨日期
    @NotNull(message = "到貨日期不可為空")
    @FutureOrPresent(message = "預計到貨日不可早於今天")
    private LocalDate expectedDeliveryDate;

    // 採購總金額
    @NotEmpty(message = "採購單至少要有一筆明細")
    @Valid
    private List<@Valid PurchaseOrderItemsCreDTO> items;
}