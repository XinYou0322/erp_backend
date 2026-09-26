package com.example.demo.inventorylog;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import com.example.demo.materials.Material;


@Entity
@Table(name = "inventory_logs")
@Data
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(length = 50)
    private String action;

    @Column(name = "ref_id")
    private Long refId;

    // 【本次新增：銷售與庫存同步】
    // 記錄銷售時實際扣除的庫存批次 ID，讓銷售單報廢時能把數量精確回補到原批次。
    // 對應資料庫欄位由 database-upgrade.sql 的預存程序建立。
    @Column(name = "inventory_batch_id")
    private Long inventoryBatchId;

    @Column(length = 255)
    private String note;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
