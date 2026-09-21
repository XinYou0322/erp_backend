package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.suppliers.Suppliers;

public interface SalesOrderRepository extends JpaRepository<SalesOrders, Long>{
	
	//Ex.20260914-001
	long countByOrderNumberStartingWith(String date);

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
}
