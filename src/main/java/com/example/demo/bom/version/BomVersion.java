package com.example.demo.bom.version;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.products.Products;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;

@Entity
@Table(name = "bom_versions", uniqueConstraints =
        @UniqueConstraint(name = "uk_bom_version_product_number", columnNames = { "product_id", "version_number" }))
@Getter
@Setter
public class BomVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "change_type", nullable = false, length = 30)
    private String changeType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BomVersionItem> items = new ArrayList<>();

    public void addItem(BomVersionItem item) {
        items.add(item);
        item.setVersion(this);
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
