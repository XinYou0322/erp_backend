package com.example.demo.bom;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import com.example.demo.materials.Material;
import com.example.demo.products.Products;

@Entity
@Table(name = "bom")
@Data
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 此配方屬於哪個產品
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "product_id",
        nullable = false
    )
    private Products product;


    // 此配方使用哪個原物料
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "material_id",
        nullable = false
    )
    private Material material;


    // 製作一個產品需要使用多少原物料
    @Column(
        nullable = false,
        precision = 18,
        scale = 4
    )
    private BigDecimal quantity;
}