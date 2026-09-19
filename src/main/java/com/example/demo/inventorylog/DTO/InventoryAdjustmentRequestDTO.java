package com.example.demo.inventorylog.DTO;

import java.util.List;

import lombok.Data;

@Data
public class InventoryAdjustmentRequestDTO {

    private List<InventoryAdjustmentItemDTO> items;
}