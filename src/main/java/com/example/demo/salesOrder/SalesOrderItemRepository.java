package com.example.demo.salesOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    @Query("""
                SELECT i.productName, SUM(i.quantity)
                FROM SalesOrderItem i
                WHERE i.salesOrder.status = 'COMPLETED'
                GROUP BY i.productName
                ORDER BY SUM(i.quantity) DESC
            """)
    List<Object[]> findTopProducts(Pageable pageable);

    @Query("""
                SELECT i.productName, SUM(i.quantity)
                FROM SalesOrderItem i
                WHERE i.salesOrder.status = com.example.demo.salesOrder.SalesOrderStatus.COMPLETED
                AND i.salesOrder.createdAt >= :startDate
                GROUP BY i.productName
                ORDER BY SUM(i.quantity) DESC
            """)
    List<Object[]> findTopProductsSince(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    @Query("""
        SELECT i.productName, SUM(i.subtotal)
        FROM SalesOrderItem i
        WHERE i.salesOrder.status = :status
        GROUP BY i.productName
        ORDER BY SUM(i.subtotal) DESC
    """)
    List<Object[]> findTopProductsByRevenue(
        @Param("status") SalesOrderStatus status,
        Pageable pageable);

    @Query("""
        SELECT i.productName, SUM(i.subtotal)
        FROM SalesOrderItem i
        WHERE i.salesOrder.status = :status
        AND i.salesOrder.createdAt >= :startDate
        GROUP BY i.productName
        ORDER BY SUM(i.subtotal) DESC
    """)
    List<Object[]> findTopProductsByRevenueSince(
        @Param("status") SalesOrderStatus status,
        @Param("startDate") LocalDateTime startDate,
        Pageable pageable);

    @Query(value = """
        SELECT
            DATEPART(HOUR, so.created_at) AS hour,
            SUM(soi.quantity) AS quantity
        FROM sales_order_items soi
        JOIN sales_orders so ON soi.sales_order_id = so.id
        WHERE so.status = :status
        AND CAST(so.created_at AS DATE) = :date
        GROUP BY DATEPART(HOUR, so.created_at)
        ORDER BY hour
        """, nativeQuery = true)
    List<Object[]> findHourlySales(
            @Param("status") String status,
            @Param("date") LocalDate date);

    @Query("""
        SELECT b.material.id, b.material.code, b.material.name, b.material.unit,
               SUM(i.quantity * b.quantity)
        FROM SalesOrderItem i
        JOIN Bom b ON b.product = i.product
        WHERE i.salesOrder.status = :status
          AND i.salesOrder.createdAt >= :start
          AND i.salesOrder.createdAt < :end
        GROUP BY b.material.id, b.material.code, b.material.name, b.material.unit
        ORDER BY b.material.code
        """)
    List<Object[]> sumMaterialUsageBySales(
            @Param("status") SalesOrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
        SELECT b.material.id, b.material.code, b.material.name, b.material.unit,
               SUM(i.quantity * b.quantity)
        FROM SalesOrderItem i
        JOIN Bom b ON b.product = i.product
        WHERE i.salesOrder.status = :status
          AND i.salesOrder.id IN :salesOrderIds
        GROUP BY b.material.id, b.material.code, b.material.name, b.material.unit
        ORDER BY b.material.code
        """)
    List<Object[]> sumMaterialUsageBySalesOrderIds(
            @Param("status") SalesOrderStatus status,
            @Param("salesOrderIds") List<Long> salesOrderIds);

}
