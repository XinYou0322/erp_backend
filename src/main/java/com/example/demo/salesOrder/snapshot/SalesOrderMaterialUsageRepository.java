package com.example.demo.salesOrder.snapshot;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderMaterialUsageRepository extends JpaRepository<SalesOrderMaterialUsage, Long> {
    List<SalesOrderMaterialUsage> findBySalesOrderIdOrderByIdAsc(Long salesOrderId);
    boolean existsBySalesOrderId(Long salesOrderId);
}
