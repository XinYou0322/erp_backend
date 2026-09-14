package com.example.demo.inventorylog;

import java.util.List;

import lombok.Data;

@Data
public class InventoryAdjustmentRequestDTO {

    private List<InventoryAdjustmentItemDTO> items;
}