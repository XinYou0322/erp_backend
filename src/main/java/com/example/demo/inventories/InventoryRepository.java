package com.example.demo.inventories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.materials.Material;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    // 核心查詢：透過原物料的 ID，找出該原物料目前的即時庫存

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.material.id = :materialId
            ORDER BY
                CASE
                    WHEN i.quantity <= 0 THEN 1
                    ELSE 0
                END,
                CASE
                    WHEN i.expiryDate IS NULL THEN 1
                    ELSE 0
                END,
                i.expiryDate ASC
        """)
        List<Inventory> findByMaterialIdOrderForStock(
                @Param("materialId") Long materialId
        );
    
	List<Inventory> findByMaterialIdOrderByExpiryDateAsc(Long materialId);

    @Query("""
        SELECT i.material.id,
               SUM(CASE WHEN i.quantity > 0 AND (i.expiryDate IS NULL OR i.expiryDate >= :today)
                        THEN i.quantity ELSE 0 END),
               SUM(CASE WHEN i.quantity > 0 AND i.expiryDate < :today
                        THEN i.quantity ELSE 0 END)
        FROM Inventory i
        GROUP BY i.material.id
    """)
    List<Object[]> sumAvailableAndExpiredByMaterial(@Param("today") LocalDate today);
	

    
}
