package com.example.demo.inventorylog;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    
    // 未來提供給 D 同學（報表統計）查看特定原料的所有異動軌跡
    List<InventoryLog> findByMaterialIdOrderByCreatedAtDesc(Long materialId);
}