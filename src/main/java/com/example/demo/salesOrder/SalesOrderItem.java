package com.example.demo.salesOrder;

import java.math.BigDecimal;

import org.hibernate.annotations.ManyToAny;

import com.example.demo.products.Products;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "sales_order_items")
@Getter
@Setter
@NoArgsConstructor
public class SalesOrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	@JoinColumn(name = "sales_order_id", nullable = false)
	private SalesOrders salesOrder;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Products product;

//    // 快照欄位：商品日後改名或改價，舊訂單仍保留結帳當下內容。
//	  //結帳當下商品編號
//    @Column(name = "product_sku", nullable = false, length = 50)
//    private String productSku;
//
//	  //結帳當下商品名稱
//    @Nationalized
//    @Column(name = "product_name", nullable = false, length = 100)
//    private String productName;
	
	@Column(nullable = false)
	private Integer quantity;
	
	//結帳當下的單價
	@Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;
	
	@Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal;
	
	
}
