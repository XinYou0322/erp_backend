package com.example.demo.inventorylog;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    
    // 未來提供給 D 同學（報表統計）查看特定原料的所有異動軌跡
    List<InventoryLog> findByMaterialIdOrderByCreatedAtDesc(Long materialId);
    
    List<InventoryLog> findAllByOrderByCreatedAtDesc();

    // 【本次新增：銷售與庫存同步】
    // 檢查同一張銷售單是否已有扣除或回補紀錄，防止重複扣庫存及重複回補。
    boolean existsByActionAndRefId(String action, Long refId);

    // 【本次新增：銷售與庫存同步】
    // 依銷售單 ID 取得原始扣庫存紀錄，報廢時按照紀錄逐批回補。
    List<InventoryLog> findByActionAndRefIdOrderByIdAsc(String action, Long refId);

    List<InventoryLog>
    findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
        Instant start,
        Instant end
    );
}
