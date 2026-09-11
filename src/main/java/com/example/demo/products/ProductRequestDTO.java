package com.example.demo.products;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRequestDTO {

    private String sku;

    private String name;

    // 前端只需要傳分類 ID
    private Long categoryId;

    private BigDecimal sellingPrice;

    private String unit;

    private String status;
}