package com.example.demo.inventories;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryBatchDTO {

    // 庫存批次 ID
    private Long inventoryId;

    // 此批剩餘數量
    private BigDecimal quantity;

    // 此批有效期限
    private LocalDate expiryDate;

    // 此批建立時間
    private Instant createdAt;
}