package com.example.demo.suppliers;

import com.example.demo.purchaseOrder.PurchaseOrders;
import com.example.demo.suppliersNotes.SupplierNotes;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
public class Suppliers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Nationalized //讓資料庫支持中文
	@Column(nullable = false,length = 50)
	private String name;
	
	// 國別
	@Column(name = "country_calling_code",nullable = false, length = 10)
    private String callingCode = "+886";
	
	@Column(nullable = false,length = 50)
	private String phone;
	
	//分機
	@Column(length = 10)
    private String extension;
	
	@Nationalized //讓資料庫支持中文
	@Column(nullable = false,length = 200)
	private String address;
	
	@Column(nullable = false, unique = true,length = 50)
	private String email;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	//@ColumnDefault("'PENDING'") 資料庫欄位的預設值
	private SupplierStatus status = SupplierStatus.PENDING; //預設為審核中
	
	@JsonIgnore
	@OneToMany (mappedBy = "supplier")
//	@OrderBy("createdAt DESC")
	private List<SupplierNotes> supplierNotes = new LinkedList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "supplier")
    private List<PurchaseOrders> purchaseOrders = new LinkedList<>();
	
	//以防被惡意修改
	@PrePersist //Entity 真正執行 INSERT 之前，先執行這個方法
	protected void applyDefaultStatus() {
		if (status == null) {
			status = SupplierStatus.PENDING;
		}
	}
	
	public void addNote(SupplierNotes note) {
	    supplierNotes.add(note);
	    note.setSupplier(this);
	}
}
