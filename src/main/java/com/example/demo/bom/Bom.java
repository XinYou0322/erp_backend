package com.example.demo.bom;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import com.example.demo.materials.Materials;
import com.example.demo.products.Products;

@Entity
@Table(name = "bom")
@Data
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多筆配方紀錄可以對應到同一個商品（對應你的 product_id）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products product;

    // 多筆配方紀錄可以對應到同一個原物料（對應你的 material_id）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Materials material;

    // 對應你的 quantity，使用 BigDecimal 確保高精度的公克或毫升數
    @Column(precision = 18, scale = 4)
    private BigDecimal quantity;
}