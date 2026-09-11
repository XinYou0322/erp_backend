package com.example.demo.bom;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BomItemRequestDTO {

    private Long materialId;

    private BigDecimal quantity;
}