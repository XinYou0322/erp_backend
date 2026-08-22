package com.example.demo.suppliers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "suppliers")
@Data
public class Suppliers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@mapping 到 purchase_orders - supplier_id
	private Long id;
	
	@Column(length = 50)
	private String name;
	
	@Column(length = 50)
	private Integer phone;
	
	@Column(length = 50)
	private String address;
	
	//資料庫還沒有 email 欄位
	@Column(length = 50)
	private String email;
	

}
