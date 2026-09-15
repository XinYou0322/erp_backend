package com.example.demo.materials;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "materials")
@Data
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(name = "cost_mode",nullable = false, length = 50)
    private String costMode;

    @Column(name = "purchase_unit",nullable = true, length = 50)
    private String purchaseUnit;
    
    
    //轉換單位
    @Column(name = "conversion_quantity",nullable = true, precision = 18, scale = 4)
    private BigDecimal conversionQuantity;
    //採購成本
    @Column(name = "purchase_cost",nullable = true, precision = 18, scale = 4)
    private BigDecimal purchaseCost;

    
    
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal cost;

    // 安全庫存
    @Column(name = "safety_stock", precision = 18, scale = 4)
    private BigDecimal safetyStock;
}