package com.example.demo.purchase;

import java.math.BigDecimal;

import com.example.demo.materials.Material;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_order_items")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderItems{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id") //採購單id
    private PurchaseOrders purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "material_id") //物料id 對面的OneToMany需不需要做關聯
    private Material material;

    @Column(nullable = false, precision = 18, scale = 3) //???????
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 2) ////????
    private BigDecimal price;
}