package com.example.demo.materialsettlement;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.users.User;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "daily_material_settlements",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_daily_material_settlement_date",
                columnNames = "settlement_date"))
@Getter
@Setter
@NoArgsConstructor
public class DailyMaterialSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyMaterialSettlementItem> items = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (status == null) status = SettlementStatus.DRAFT;
        if (createdAt == null) createdAt = Instant.now();
    }

    public void replaceItems(List<DailyMaterialSettlementItem> newItems) {
        items.clear();
        for (DailyMaterialSettlementItem item : newItems) {
            item.setSettlement(this);
            items.add(item);
        }
    }
}
