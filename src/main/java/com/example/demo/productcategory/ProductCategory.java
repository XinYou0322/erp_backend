package com.example.demo.productcategory;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_categories")
@Data
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 分類名稱，例如：茶類、奶茶、果茶
    @Column(nullable = false, unique = true, length = 50)
    private String name;


    // 是否啟用
    @Column(nullable = false)
    private Boolean active = true;
}