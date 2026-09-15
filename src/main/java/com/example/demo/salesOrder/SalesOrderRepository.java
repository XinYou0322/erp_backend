package com.example.demo.salesOrder;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.suppliers.Suppliers;

public interface SalesOrderRepository extends JpaRepository<SalesOrders, Long>{
	
	//Ex.20260914-001
	long countByOrderNumberStartingWith(String date);
}
