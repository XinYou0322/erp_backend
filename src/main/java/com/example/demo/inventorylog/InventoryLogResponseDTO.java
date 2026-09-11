package com.example.demo.inventorylog;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

@Data
public class InventoryLogResponseDTO {

    private Long id;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String unit;

    private BigDecimal quantity;

    private String action;

    private Long refId;

    private String note;

    private Instant createdAt;
}