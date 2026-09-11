package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.Nationalized;

import com.example.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
public class SalesOrders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//編號20260910-001
	@Column(name = "order_number", nullable = false, unique = true, length = 40)
	private String orderNumber;
	
	//這個欄位是 Java 的 enum，而且要用「文字名稱STRING」存進資料庫。
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SalesOrderStatus status = SalesOrderStatus.COMPLETED;//預設交易已完成
	
	//這個欄位是 Java 的 enum，而且要用「文字名稱STRING」存進資料庫。
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 30)
	private PaymentMethod paymentMethod;
	
	//總額 precision數字總共可以有幾位 / scale小數點後可以有幾位
	@Column( name = "total_amount", nullable = false, precision = 18, scale = 2)
	private BigDecimal totalAmount;
	
	//訂單建立人
	//optional = false 這個關聯是必須存在的，不能沒有對應的物件。
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)	 
	private User createdBy;
	
	//誰把這筆訂單作廢
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voided_by_user_id")
	private User voidedBy;
	
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	//作廢時間
	@Column(name = "voided_at")
	private LocalDateTime voidedAt;
	
	//備註
	@Nationalized
	@Column(length = 200)
	private String note;
	
	//作廢理由
    @Nationalized
    @Column(name = "void_reason", length = 1000)
    private String voidReason;
    
    @JsonIgnore
    @OneToMany(mappedBy = "salesOrder",  orphanRemoval = true)
    //@OrderBy("id ASC")
    private List<SalesOrderItem> items = new LinkedList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    
    
    
    
	
}
