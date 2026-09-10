package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.Nationalized;

import com.example.demo.suppliers.Suppliers;
import com.example.demo.users.User;
import com.example.demo.workflow.enums.WorkflowStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.demo.purchaseOrderItem.PurchaseOrderItems;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false )
    private Suppliers supplier;
	
	//未
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private WorkflowStatus status = WorkflowStatus.PENDING;

	//申請人
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    private User createdBy;
	
    //簽核人
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Column( name="approved_by_user_id", nullable = false, updatable = false)
	private User approvedBy;
    
    //收貨人
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by_user_id")
    private User receivedBy;
	
	//precision-總位數 scale-小數位數
	//訂單總價map
	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal total = BigDecimal.ZERO;
	
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	//預計到貨日
	@Column(name = "expected_delivery_date")
	private LocalDate expectedDeliveryDate;
	
	@Column(name = "received_at")
	private LocalDateTime receivedAt;

	//放收據的地方
	@Column(name = "receipt_url", length = 500)
	private String receiptUrl;
	
	//備註
	@Nationalized
	@Column(name = "decision_remark", length = 1000)
	private String decisionRemark;
	
	
	// 一張採購單有多筆採購明細
    @OneToMany(mappedBy = "purchaseOrder")
    //@OrderBy("id ASC") 取得明細時id 小 → 大
    private List<PurchaseOrderItems> items = new LinkedList<>();

    //雙向關聯
    public void addItem(PurchaseOrderItems item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }
    
    
	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
	    createdAt = now;
	    updatedAt = now;
	    }

}
