package com.example.demo.salesOrder;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    @Query("""
        SELECT i.productName, SUM(i.quantity)
        FROM SalesOrderItem i
        WHERE i.salesOrder.status = 'COMPLETED'
        GROUP BY i.productName
        ORDER BY SUM(i.quantity) DESC
    """)
    List<Object[]> findTopProducts(Pageable pageable);
}