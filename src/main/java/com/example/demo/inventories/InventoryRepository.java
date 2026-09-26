package com.example.demo.inventories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.materials.Material;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

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

    // 【本次新增：銷售與庫存同步】
    // 銷售扣庫存前，以寫入鎖鎖定該原物料的所有庫存批次。
    // 批次依「有效庫存、效期、批次 ID」排序，避免多人同時結帳造成超賣，並優先扣除較早到期的庫存。
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.material.id = :materialId
            ORDER BY
                CASE WHEN i.quantity <= 0 THEN 1 ELSE 0 END,
                CASE WHEN i.expiryDate IS NULL THEN 1 ELSE 0 END,
                i.expiryDate ASC,
                i.id ASC
        """)
    List<Inventory> findByMaterialIdOrderForStockWithLock(
            @Param("materialId") Long materialId
    );

    // 【本次新增：銷售與庫存同步】
    // 銷售單報廢回補時鎖定原扣除批次，避免回補期間被其他交易同時修改。
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findByIdWithLock(@Param("id") Long id);
	

    
}
