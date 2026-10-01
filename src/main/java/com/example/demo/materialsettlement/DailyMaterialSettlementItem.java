package com.example.demo.materialsettlement;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.example.demo.materials.Material;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "daily_material_settlement_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_settlement_material",
                columnNames = { "settlement_id", "material_id" }))
@Getter
@Setter
@NoArgsConstructor
public class DailyMaterialSettlementItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private DailyMaterialSettlement settlement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "manual_issue_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal manualIssueQuantity;

    @Column(name = "previous_carryover_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal previousCarryoverQuantity;

    @Column(name = "theoretical_usage_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal theoreticalUsageQuantity;

    @Column(name = "waste_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal wasteQuantity;

    @Column(name = "unrecorded_usage_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal unrecordedUsageQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unrecorded_reason", length = 40)
    private UnrecordedUsageReason unrecordedReason;

    @Column(name = "unrecorded_note", length = 500)
    private String unrecordedNote;

    @Column(name = "workspace_carryover_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal workspaceCarryoverQuantity;

    @Column(name = "returned_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal returnedQuantity;

    @Column(name = "returned_expiry_date")
    private LocalDate returnedExpiryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
        applyDefaults();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
        applyDefaults();
    }

    private void applyDefaults() {
        if (manualIssueQuantity == null) manualIssueQuantity = BigDecimal.ZERO;
        if (previousCarryoverQuantity == null) previousCarryoverQuantity = BigDecimal.ZERO;
        if (theoreticalUsageQuantity == null) theoreticalUsageQuantity = BigDecimal.ZERO;
        if (wasteQuantity == null) wasteQuantity = BigDecimal.ZERO;
        if (unrecordedUsageQuantity == null) unrecordedUsageQuantity = BigDecimal.ZERO;
        if (workspaceCarryoverQuantity == null) workspaceCarryoverQuantity = BigDecimal.ZERO;
        if (returnedQuantity == null) returnedQuantity = BigDecimal.ZERO;
    }
}
