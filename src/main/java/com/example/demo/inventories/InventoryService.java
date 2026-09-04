package com.example.demo.inventories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public List<InventorySummaryDTO> getInventorySummary() {///茶每一批原料總共多少輛

        List<Material> materials = materialRepository.findAll();

        return materials.stream()
                .map(material -> {

                    List<Inventory> batches =
                            inventoryRepository
                                    .findByMaterialIdOrderByExpiryDateAsc(
                                            material.getId()
                                    );

                    BigDecimal totalQuantity = batches.stream()
                            .map(Inventory::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    LocalDate nearestExpiryDate = batches.stream()
                            .filter(batch ->
                                    batch.getQuantity() != null
                                    && batch.getQuantity()
                                            .compareTo(BigDecimal.ZERO) > 0
                            )
                            .map(Inventory::getExpiryDate)
                            .filter(date -> date != null)
                            .min(LocalDate::compareTo)
                            .orElse(null);
                    BigDecimal safetyStock = material.getSafetyStock();
                    
                    String status = "NORMAL";
                    
                    if (safetyStock != null) {

                        if (totalQuantity.compareTo(safetyStock.multiply(new BigDecimal("0.4"))) <= 0) {
                            status = "URGENT";

                        } else if (totalQuantity.compareTo(safetyStock) <= 0) {
                            status = "LOW";
                        }

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
                    	    nearestExpiryDate
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

    // 刪除一批（例如整批報廢或輸入錯誤）
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}