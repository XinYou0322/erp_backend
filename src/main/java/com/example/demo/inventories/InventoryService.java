package com.example.demo.inventories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.inventorylog.InventoryLog;
import com.example.demo.inventorylog.InventoryLogRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    public final InventoryRepository inventoryRepository;
    public final InventoryLogRepository inventoryLogRepository;

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

    // 查詢某原物料的所有批次
    public List<Inventory> findByMaterialId(Long materialId) {
        return inventoryRepository.findByMaterialIdOrderByExpiryDateAsc(materialId);
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