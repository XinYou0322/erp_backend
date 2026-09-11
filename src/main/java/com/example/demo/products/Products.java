package com.example.demo.products;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import com.example.demo.productcategory.ProductCategory;

@Entity
@Table(name = "products")
@Data
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 商品代碼
    @Column(nullable = false, unique = true, length = 50)
    private String sku;


    // 商品名稱
    @Column(nullable = false, length = 100)
    private String name;


    // 商品分類
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;
    // 商品售價
    @Column(
        name = "selling_price",
        nullable = false,
        precision = 18,
        scale = 2
    )
    private BigDecimal sellingPrice;


    // 單杯成本
    // 未來由 BOM 原物料成本計算
    @Column(
        name = "cost_price",
        precision = 18,
        scale = 2
    )
    private BigDecimal costPrice;


    // 銷售單位，例如：杯
    @Column(nullable = false, length = 50)
    private String unit;


    // 商品狀態，例如 ACTIVE / INACTIVE
    @Column(nullable = false, length = 50)
    private String status;
}