package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.suppliers.Suppliers;

public interface SalesOrderRepository extends JpaRepository<SalesOrders, Long> {

	// Ex.20260914-001
	long countByOrderNumberStartingWith(String date);

	@Query(value = """
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
					""")
	Page<SalesOrders> searchSalesOrders(

			@Param("status") SalesOrderStatus status,

			@Param("paymentMethod") PaymentMethod paymentMethod,

			@Param("createdById") Long createdById,

			@Param("startDateTime") LocalDateTime startDateTime,

			@Param("endDateTime") LocalDateTime endDateTime,

			@Param("minAmount") BigDecimal minAmount,

			@Param("maxAmount") BigDecimal maxAmount,

			@Param("keyword") String keyword,

			Pageable pageable);

	// 今日營收
	@Query("""
			SELECT COALESCE(SUM(s.totalAmount),0)
			FROM SalesOrders s
			WHERE s.status='COMPLETED'
			AND s.createdAt BETWEEN :start AND :end
			""")
	BigDecimal getRevenueBetween(
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	// 今日訂單數
	@Query("""
			SELECT COUNT(s)
			FROM SalesOrders s
			WHERE s.status='COMPLETED'
			AND s.createdAt BETWEEN :start AND :end
			""")
	Long countOrdersBetween(
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	// 平均客單價
	@Query("""
			SELECT COALESCE(AVG(s.totalAmount),0)
			FROM SalesOrders s
			WHERE s.status='COMPLETED'
			AND s.createdAt BETWEEN :start AND :end
			""")
	Double getAverageOrderBetween(
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	// 最近七天營收（不含今天）
	@Query("""
			SELECT CAST(s.createdAt AS date), SUM(s.totalAmount)
			FROM SalesOrders s
			WHERE s.status='COMPLETED'
			AND s.createdAt>=:start
			AND s.createdAt<:end
			GROUP BY CAST(s.createdAt AS date)
			ORDER BY CAST(s.createdAt AS date)
			""")
	List<Object[]> getWeeklyRevenue(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	// 1. 按「天」分組 (優化版，取代或並行於原本的 getWeeklyRevenue)
	@Query("""
			SELECT CAST(s.createdAt AS date), SUM(s.totalAmount)
			FROM SalesOrders s
			WHERE s.status='COMPLETED'
			AND s.createdAt >= :start
			AND s.createdAt <= :end
			GROUP BY CAST(s.createdAt AS date)
			ORDER BY CAST(s.createdAt AS date)
			""")
	List<Object[]> getRevenueGroupedByDay(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	// 2. 按「月」分組 (回傳: [Year, Month, SumAmount])
	@Query("""
			SELECT YEAR(s.createdAt), MONTH(s.createdAt), SUM(s.totalAmount)
			FROM SalesOrders s
			WHERE s.status='COMPLETED'
			AND s.createdAt >= :start
			AND s.createdAt <= :end
			GROUP BY YEAR(s.createdAt), MONTH(s.createdAt)
			ORDER BY YEAR(s.createdAt), MONTH(s.createdAt)
			""")
	List<Object[]> getRevenueGroupedByMonth(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	// 3. 按「年」分組 (回傳: [Year, SumAmount])
	@Query("""
			SELECT YEAR(s.createdAt), SUM(s.totalAmount)
			FROM SalesOrders s
			WHERE s.status='COMPLETED'
			AND s.createdAt >= :start
			AND s.createdAt <= :end
			GROUP BY YEAR(s.createdAt)
			ORDER BY YEAR(s.createdAt)
			""")
	List<Object[]> getRevenueGroupedByYear(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

}
