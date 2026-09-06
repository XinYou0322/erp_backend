package com.example.demo.suppliers;

import com.example.demo.purchase.PurchaseOrders;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	@Column(length = 50)
	private String name;
	
	
	@Column(length = 50)
	private String phone;
	
	@Nationalized //讓資料庫支持中文
	@Column(length = 50)
	private String address;
	
	//資料庫還沒有 email 欄位
	@Column(length = 50)
	private String email;
	
	@OneToMany(mappedBy = "supplier")
    private List<PurchaseOrders> purchaseOrders = new LinkedList<>();
}
