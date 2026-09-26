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
    
    @Column(nullable = false, length = 20)   
    private String status;
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

    // 從下單到預計到貨所需天數，供補貨需求預測使用。
    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays;

    // 每一個採購包裝可換算成多少庫存單位，供建議採購量向上取整使用。
    @Column(name = "purchase_pack_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal purchasePackQuantity;

    @PrePersist
    protected void applyPurchasingDefaults() {
        if (leadTimeDays == null) {
            leadTimeDays = 7;
        }
        if (purchasePackQuantity == null) {
            purchasePackQuantity = BigDecimal.ONE;
        }
    }
}
