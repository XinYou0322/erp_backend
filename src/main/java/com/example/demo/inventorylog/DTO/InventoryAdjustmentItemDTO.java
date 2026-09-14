package com.example.demo.inventorylog;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InventoryAdjustmentItemDTO {

    private Long inventoryId;

    private String action;

    private BigDecimal quantity;

    private String note;
}