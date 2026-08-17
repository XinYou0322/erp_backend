package com.example.demo.inventories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventories, Long> {
    
    // 核心查詢：透過原物料的 ID，找出該原物料目前的即時庫存
    Optional<Inventories> findByMaterialId(Long materialId);
}