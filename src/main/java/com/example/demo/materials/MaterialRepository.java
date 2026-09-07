package com.example.demo.materials;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface  MaterialRepository extends JpaRepository<Material, Long> {
	  java.util.Optional<Material> findByCode(String code);
	  
	    @Query("""
	            SELECT AVG(m.cost)
	            FROM Material m
	        """)
	        BigDecimal findAverageCost();


	        // 有設定安全庫存的原物料數量
	        @Query("""
	            SELECT COUNT(m)
	            FROM Material m
	            WHERE m.safetyStock > 0
	        """)
	        Long countSafetyStockMaterials();


	        // 計量單位種類
	        @Query("""
	            SELECT COUNT(DISTINCT m.unit)
	            FROM Material m
	        """)
	        Long countDistinctUnits();
}
