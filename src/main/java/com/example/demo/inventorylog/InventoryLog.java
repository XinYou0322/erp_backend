package com.example.demo.inventorylog;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.materials.Materials;

@Entity
@Table(name = "inventory_logs")
@Data
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 歷史紀錄會有多筆對應到同一個原物料 (對應你的 material_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Materials material;

    // 本次異動數量 (進貨存正數如 50.00，銷售存負數如 -0.05)
    @Column(precision = 18, scale = 4)
    private BigDecimal quantity;

    // 異動動作，如: SALE_DEDUCT(銷售扣減), STOCK_IN(進貨增加)
    @Column(length = 50)
    private String action;

    // 關聯單據 ID (銷售就存 POS 訂單 ID，進貨就存進貨單 ID)
    @Column(name = "ref_id")
    private Long refId;

    // 對應你的 timestamp，在 Java 中使用 LocalDateTime 最為標準
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 在資料寫入資料庫前，自動幫你塞入當前系統時間
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}