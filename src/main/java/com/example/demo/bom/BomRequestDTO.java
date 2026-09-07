package com.example.demo.bom;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BomRequestDTO {

    private Long productId;

    private Long materialId;

    private BigDecimal quantity;
}