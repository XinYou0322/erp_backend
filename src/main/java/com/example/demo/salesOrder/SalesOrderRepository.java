package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.suppliers.Suppliers;

public interface SalesOrderRepository extends JpaRepository<SalesOrders, Long>{
	
	//Ex.20260914-001
	long countByOrderNumberStartingWith(String date);
	
	@Query(
	        value = """
	            SELECT DISTINCT so
	            FROM SalesOrders so

	            LEFT JOIN so.items item

	            WHERE
	                (:status IS NULL
	                    OR so.status = :status)

	            AND
	                (:paymentMethod IS NULL
	                    OR so.paymentMethod = :paymentMethod)

	            AND
	                (:createdById IS NULL
	                    OR so.createdBy.id = :createdById)

	            AND
	                (:startDateTime IS NULL
	                    OR so.createdAt >= :startDateTime)

	            AND
	                (:endDateTime IS NULL
	                    OR so.createdAt < :endDateTime)

	            AND
	                (:minAmount IS NULL
	                    OR so.totalAmount >= :minAmount)

	            AND
	                (:maxAmount IS NULL
	                    OR so.totalAmount <= :maxAmount)

	            AND
	                (
	                    :keyword IS NULL

	                    OR LOWER(so.orderNumber)
	                       LIKE LOWER(CONCAT('%', :keyword, '%'))

	                    OR LOWER(item.productName)
	                       LIKE LOWER(CONCAT('%', :keyword, '%'))

	                    OR LOWER(item.productSku)
	                       LIKE LOWER(CONCAT('%', :keyword, '%'))
	                )
	            """,

	        countQuery = """
	            SELECT COUNT(DISTINCT so.id)
	            FROM SalesOrders so

	            LEFT JOIN so.items item

	            WHERE
	                (:status IS NULL
	                    OR so.status = :status)

	            AND
	                (:paymentMethod IS NULL
	                    OR so.paymentMethod = :paymentMethod)

	            AND
	                (:createdById IS NULL
	                    OR so.createdBy.id = :createdById)

	            AND
	                (:startDateTime IS NULL
	                    OR so.createdAt >= :startDateTime)

	            AND
	                (:endDateTime IS NULL
	                    OR so.createdAt < :endDateTime)

	            AND
	                (:minAmount IS NULL
	                    OR so.totalAmount >= :minAmount)

	            AND
	                (:maxAmount IS NULL
	                    OR so.totalAmount <= :maxAmount)

	            AND
	                (
	                    :keyword IS NULL

	                    OR LOWER(so.orderNumber)
	                       LIKE LOWER(CONCAT('%', :keyword, '%'))

	                    OR LOWER(item.productName)
	                       LIKE LOWER(CONCAT('%', :keyword, '%'))

	                    OR LOWER(item.productSku)
	                       LIKE LOWER(CONCAT('%', :keyword, '%'))
	                )
	            """
	    )
	    Page<SalesOrders> searchSalesOrders(

	            @Param("status")
	            SalesOrderStatus status,

	            @Param("paymentMethod")
	            PaymentMethod paymentMethod,

	            @Param("createdById")
	            Long createdById,

	            @Param("startDateTime")
	            LocalDateTime startDateTime,

	            @Param("endDateTime")
	            LocalDateTime endDateTime,

	            @Param("minAmount")
	            BigDecimal minAmount,

	            @Param("maxAmount")
	            BigDecimal maxAmount,

	            @Param("keyword")
	            String keyword,

	            Pageable pageable
	    );
}
