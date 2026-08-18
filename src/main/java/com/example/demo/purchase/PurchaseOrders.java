package com.example.demo.purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "purchase_orders")
@Data
public class PurchaseOrders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@mapping 到 purchase_order_items - [purchase_order_id]
	private Long id;
	
	@Column(name="supplier_id")
	//@mapping 到 supplier - [id]
	private Long supplierId;
	
	@Column(length = 50)
	private String status;
	
	//誰建立的訂購單
	@Column(name="created_by",length = 50)
	//@mapping到 ????
	private String createdBy;
	
	@Column(name="approved_by",length = 50)
	//@mapping到 ????
	private String approvedBy;
	
	//precision-總位數 scale-小數位數
	@Column(precision = 18, scale = 0)
	private BigDecimal total;
	
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	    
	@PrePersist
	protected void onCreate() {
	   this.createdAt = LocalDateTime.now();
	    }

}
