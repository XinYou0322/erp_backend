package com.example.demo.inventories;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.inventories.Inventory;
import com.example.demo.inventorylog.InventoryLog;
import com.example.demo.inventorylog.InventoryLogRepository;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    public final InventoryRepository inventoryRepository;
    public final InventoryLogRepository inventoryLogRepository;
public final MaterialRepository materialRepository;
    // 新增一批（進貨），同步記錄一筆 STOCK_IN log
    @Transactional(rollbackFor = Exception.class)
    public Inventory create(Inventory inventory) {

        Inventory saved = inventoryRepository.save(inventory);

        InventoryLog log = new InventoryLog();
        log.setMaterial(saved.getMaterial());
        log.setQuantity(saved.getQuantity());
        log.setAction("STOCK_IN");
        log.setRefId(null);

        inventoryLogRepository.save(log);

        return saved;
    }
    
    public List<InventorySummaryDTO> getInventorySummary() {

        List<Material> materials =
                materialRepository.findAll();


        return materials.stream()

                .map(material -> {

                    List<Inventory> batches =
                            inventoryRepository
                                    .findByMaterialIdOrderByExpiryDateAsc(
                                            material.getId()
                                    );


                    LocalDate today =
                            LocalDate.now();


                    // =========================
                    // 所有批次總庫存
                    // =========================

                    BigDecimal totalQuantity =
                            batches.stream()

                                    .map(Inventory::getQuantity)

                                    .filter(quantity ->
                                            quantity != null
                                    )

                                    .reduce(
                                            BigDecimal.ZERO,
                                            BigDecimal::add
                                    );


                    // =========================
                    // 已過期庫存數量
                    // =========================

                    BigDecimal expiredQuantity =
                            batches.stream()

                                    .filter(batch ->
                                            batch.getQuantity() != null
                                            &&
                                            batch.getQuantity()
                                                    .compareTo(BigDecimal.ZERO) > 0
                                    )

                                    .filter(batch ->
                                            batch.getExpiryDate() != null
                                            &&
                                            batch.getExpiryDate()
                                                    .isBefore(today)
                                    )

                                    .map(Inventory::getQuantity)

                                    .reduce(
                                            BigDecimal.ZERO,
                                            BigDecimal::add
                                    );


                    // =========================
                    // 可用庫存
                    // =========================

                    BigDecimal availableQuantity =
                            totalQuantity.subtract(
                                    expiredQuantity
                            );


                    // =========================
                    // 已過期批次數量
                    // =========================

                    int expiredBatchCount =
                            (int) batches.stream()

                                    .filter(batch ->
                                            batch.getQuantity() != null
                                            &&
                                            batch.getQuantity()
                                                    .compareTo(BigDecimal.ZERO) > 0
                                    )

                                    .filter(batch ->
                                            batch.getExpiryDate() != null
                                            &&
                                            batch.getExpiryDate()
                                                    .isBefore(today)
                                    )

                                    .count();


                    // =========================
                    // 7 天內到期批次數量
                    // =========================

                    int expiringSoonBatchCount =
                            (int) batches.stream()

                                    .filter(batch ->
                                            batch.getQuantity() != null
                                            &&
                                            batch.getQuantity()
                                                    .compareTo(BigDecimal.ZERO) > 0
                                    )

                                    .filter(batch ->
                                            batch.getExpiryDate() != null
                                    )

                                    .filter(batch -> {

                                        LocalDate expiryDate =
                                                batch.getExpiryDate();

                                        return
                                                !expiryDate.isBefore(today)
                                                &&
                                                !expiryDate.isAfter(
                                                        today.plusDays(7)
                                                );
                                    })

                                    .count();


                    // =========================
                    // 最近有效日期
                    // 這裡排除已過期批次
                    // =========================

                    LocalDate nearestExpiryDate =
                            batches.stream()

                                    .filter(batch ->
                                            batch.getQuantity() != null
                                            &&
                                            batch.getQuantity()
                                                    .compareTo(BigDecimal.ZERO) > 0
                                    )

                                    .map(Inventory::getExpiryDate)

                                    .filter(date ->
                                            date != null
                                            &&
                                            !date.isBefore(today)
                                    )

                                    .min(LocalDate::compareTo)

                                    .orElse(null);


                    // =========================
                    // 庫存狀態
                    // 改用 availableQuantity
                    // =========================

                    BigDecimal safetyStock =
                            material.getSafetyStock();


                    String status =
                            "NORMAL";


                    if (safetyStock != null) {

                        if (
                            availableQuantity.compareTo(
                                    safetyStock.multiply(
                                            new BigDecimal("0.4")
                                    )
                            ) <= 0
                        ) {

                            status =
                                    "URGENT";

                        } else if (
                            availableQuantity.compareTo(
                                    safetyStock
                            ) <= 0
                        ) {

                            status =
                                    "LOW";
                        }
                    }


                    // =========================
                    // 效期狀態
                    // =========================

                    String expiryStatus =
                            "NORMAL";


                    if (expiredBatchCount > 0) {

                        expiryStatus =
                                "EXPIRED";

                    } else if (expiringSoonBatchCount > 0) {

                        expiryStatus =
                                "EXPIRING_SOON";
                    }


                    return new InventorySummaryDTO(
                            material.getId(),
                            material.getCode(),
                            material.getName(),
                            material.getUnit(),

                            totalQuantity,
                            material.getCost(),

                            safetyStock,
                            status,
                            material.getStatus(),

                            nearestExpiryDate,

                            expiredQuantity,
                            availableQuantity,

                            expiredBatchCount,
                            expiringSoonBatchCount,

                            expiryStatus
                    );

                })

                .toList();
    }
    
    public List<InventoryBatchDTO> getInventoryBatches(Long materialId) {

        List<Inventory> batches =
            inventoryRepository
                .findByMaterialIdOrderForStock(materialId);

        return batches.stream()
            .map(batch -> new InventoryBatchDTO(
                batch.getId(),
                batch.getQuantity(),
                batch.getExpiryDate(),
                batch.getCreatedAt()
            ))
            .toList();
    }
    

    // 查詢某原物料的所有批次
    public List<Inventory> findByMaterialId(Long materialId) {

        return inventoryRepository
                .findByMaterialIdOrderForStock(materialId);
    }
    // 查詢某原物料的總庫存量（把所有批次加總）
    public BigDecimal getTotalQuantity(Long materialId) {

        List<Inventory> batches = findByMaterialId(materialId);

        BigDecimal total = BigDecimal.ZERO;

        for (Inventory batch : batches) {
            total = total.add(batch.getQuantity());
        }

        return total;
    }

    // 修改某一批（例如盤點調整數量或效期）
    public Inventory update(Long id, Inventory inventory) {

        Inventory exist = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 id=" + id + " 的批次"));

        exist.setQuantity(inventory.getQuantity());
        exist.setExpiryDate(inventory.getExpiryDate());

        return inventoryRepository.save(exist);
    }
    @Transactional

public void batchInventory(
        InventoryBatchRequestDTO request) {

    	 if (request == null ||
    		        request.getItems() == null ||
    		        request.getItems().isEmpty()) {

    		        throw new IllegalArgumentException(
    		            "進貨資料不得為空"
    		        );
    		    }
    	
    	
    for (
        InventoryBatchRequestDTO.Item item
        : request.getItems()
    ) {
    	if (item.getMaterialId() == null) {
    	    throw new IllegalArgumentException(
    	        "原物料不得為空"
    	    );
    	}

    	if (item.getQuantity() == null ||
    	    item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {

    	    throw new IllegalArgumentException(
    	        "進貨數量必須大於 0"
    	    );
    	}
        Material material =
            materialRepository
                .findById(item.getMaterialId())
                .orElseThrow(
                    () -> new RuntimeException(
                        "找不到原物料"
                    )
                );


        receive(material, item.getQuantity(), item.getExpiryDate(), null);
    }
}

    /**
     * 建立一筆進貨批次與庫存異動。quantity 使用採購單位，並沿用既有換算規則。
     */
    public Inventory receive(
            Material material,
            BigDecimal quantity,
            LocalDate expiryDate,
            Long referenceId) {
        if (material == null) {
            throw new IllegalArgumentException("原物料不得為空");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("進貨數量必須大於 0");
        }

        BigDecimal actualQuantity = quantity;
        if ("CONVERSION".equals(material.getCostMode())) {
            if (material.getConversionQuantity() == null
                    || material.getConversionQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("原物料換算數量設定錯誤");
            }
            actualQuantity = quantity.multiply(material.getConversionQuantity());
        }

        Inventory inventory = new Inventory();
        inventory.setMaterial(material);
        inventory.setExpiryDate(expiryDate);
        inventory.setQuantity(actualQuantity);
        Inventory savedInventory = inventoryRepository.save(inventory);

        InventoryLog log = new InventoryLog();
        log.setMaterial(material);
        log.setQuantity(actualQuantity);
        log.setAction("STOCK_IN");
        log.setRefId(referenceId);
        log.setCreatedAt(Instant.now());
        inventoryLogRepository.save(log);

        return savedInventory;
    }
    // 刪除一批（例如整批報廢或輸入錯誤）
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}
