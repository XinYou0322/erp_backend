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
    private ProductType productType;
    // 零售商品對應原物料的單位成本
    private BigDecimal retailCost;
    private String imageUrl;
}
