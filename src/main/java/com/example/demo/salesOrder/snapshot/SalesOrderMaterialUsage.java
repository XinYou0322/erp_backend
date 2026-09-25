package com.example.demo.salesOrder.snapshot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.salesOrder.SalesOrderItem;
import com.example.demo.salesOrder.SalesOrders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sales_order_material_usage")
@Getter
@Setter
public class SalesOrderMaterialUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrders salesOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_item_id", nullable = false)
    private SalesOrderItem salesOrderItem;

    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "product_sku", nullable = false, length = 50)
    private String productSku;
    @Column(name = "bom_version_id")
    private Long bomVersionId;
    @Column(name = "bom_version_number")
    private Integer bomVersionNumber;
    @Column(name = "material_id", nullable = false)
    private Long materialId;
    @Column(name = "material_code", nullable = false, length = 50)
    private String materialCode;
    @Column(name = "material_name", nullable = false, length = 100)
    private String materialName;
    @Column(name = "material_unit", nullable = false, length = 50)
    private String materialUnit;
    @Column(name = "product_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal productQuantity;
    @Column(name = "bom_unit_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal bomUnitQuantity;
    @Column(name = "theoretical_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal theoreticalQuantity;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
