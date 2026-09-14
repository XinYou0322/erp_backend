package com.example.demo.inventories;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InventoryBatchRequestDTO {

    private List<Item> items;

    @Data
    public static class Item {

        private Long materialId;

        private BigDecimal quantity;

        private LocalDate expiryDate;
    }
}