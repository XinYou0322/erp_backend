package com.example.demo.inventorylog;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InventoryLogRequest {

    private Long productId;

    private BigDecimal quantity;

    private Long refId;

}