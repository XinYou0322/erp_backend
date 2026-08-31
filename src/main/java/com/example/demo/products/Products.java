package com.example.demo.products;


import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 50)
    private String sku;//樣式大小

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String category;  //類別

    @Column(name = "selling_price", precision = 18, scale = 2) 
    private BigDecimal sellingPrice; // 對應你的 selling_price

    @Column(name = "cost_price", precision = 18, scale = 2)
    private BigDecimal costPrice; // 對應你的 cost_price

    @Column(length = 50)
    private String unit; //單位

    @Column(length = 50)
    private String status;//狀態
}