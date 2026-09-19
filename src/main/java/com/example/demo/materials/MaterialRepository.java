package com.example.demo.materials;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
	List<Material> findByStatus(String status);
    java.util.Optional<Material> findByCode(String code);


    // 原物料分頁：
    // ACTIVE 排前面，INACTIVE 排最後
    @Query("""
        SELECT m
        FROM Material m
        ORDER BY
            CASE
                WHEN m.status = 'ACTIVE' THEN 0
                WHEN m.status = 'INACTIVE' THEN 1
                ELSE 2
            END,
            m.id ASC
    """)
    Page<Material> findAllOrderByStatus(Pageable pageable);


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