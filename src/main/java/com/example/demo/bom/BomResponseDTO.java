package com.example.demo.bom;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BomResponseDTO {

    private Long id;

    private Long materialId;

    private String materialName;

    private String materialCode;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal materialCost;
}