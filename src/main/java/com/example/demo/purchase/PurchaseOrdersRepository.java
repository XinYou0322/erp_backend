package com.example.demo.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, Long> {
}
