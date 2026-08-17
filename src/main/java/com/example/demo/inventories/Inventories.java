package com.example.demo.inventories;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import com.example.demo.materials.Materials;

@Entity
@Table(name = "inventories")
@Data
public class Inventories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 一個原物料只會有一筆即時庫存紀錄 (對應你的 material_id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", unique = true)
    private Materials material;

    // 對應你的 quantity
    @Column(precision = 18, scale = 4)
    private BigDecimal quantity;
}