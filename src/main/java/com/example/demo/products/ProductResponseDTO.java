package com.example.demo.products;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductResponseDTO {

    private Long id;

    private String sku;

    private String name;

    private Long categoryId;

    private String categoryName;

    private BigDecimal sellingPrice;

    private BigDecimal costPrice;

    private String unit;

    private String status;
}