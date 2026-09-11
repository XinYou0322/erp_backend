package com.example.demo.purchaseOrder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, Long> {
}
