package com.example.demo.suppliersNotes;

import java.time.LocalDateTime;

import org.hibernate.annotations.Nationalized;

import com.example.demo.suppliers.Suppliers;
import com.example.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier_note")
@Getter
@Setter
@NoArgsConstructor
public class SupplierNotes {
    
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY) 
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //一筆 SupplierNotes 一定要屬於某個 Supplier
    @JoinColumn(name = "supplier_id", nullable = false)
    private Suppliers supplier;

    @Nationalized
    @Column(nullable = false)
    @Size(max = 200, message = "備註不可超過 200 字")
    private String remark;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;
    
    //修改時間
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
	protected void onCreate() {
    	 LocalDateTime now = LocalDateTime.now();

    	    createdAt = now; //建立時間
    	    updatedAt = now; //修改時間
	    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
