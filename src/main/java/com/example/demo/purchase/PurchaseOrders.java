package com.example.demo.purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.example.demo.suppliers.Suppliers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "supplier_id")
    private Suppliers supplier;
	
	@Column(length = 50)
	private String status;
	
	//誰建立的訂購單
	@Column(nullable = false,name="created_by",length = 50)
	//@mapping到 ????
	private String createdBy;
	
	@Column(nullable = false, name="approved_by",length = 50)
	//@mapping到 ????
	private String approvedBy;
	
	//precision-總位數 scale-小數位數
	@Column(nullable = false, precision = 18, scale = 0)
	private BigDecimal total;
	
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	// 一張採購單有多筆採購明細
    @OneToMany(mappedBy = "purchaseOrder")
    private List<PurchaseOrderItems> items = new LinkedList<>();

	@PrePersist
	protected void onCreate() {
	   this.createdAt = LocalDateTime.now();
	    }

}
