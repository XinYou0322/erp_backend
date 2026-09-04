package com.example.demo.inventorylog;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;
import com.example.demo.inventories.Inventory;
import com.example.demo.inventories.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryLogService {

    public final BomRepository bomRepository;
    public final InventoryRepository inventoryRepository;
    public final InventoryLogRepository inventoryLogRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deduct(Long productId, BigDecimal saleQuantity, Long refId) {

        List<Bom> bomList = bomRepository.findByProductId(productId);

        if (bomList.isEmpty()) {
            throw new IllegalStateException("此商品尚未設定配方 (BOM)");
        }

        // 第一步：先檢查所有原物料庫存是否都足夠，一個不夠就整個擋下來
        for (Bom bom : bomList) {

            Long materialId = bom.getMaterial().getId();
            BigDecimal needed = bom.getQuantity().multiply(saleQuantity);

            BigDecimal total = getTotalQuantity(materialId);

            if (total.compareTo(needed) < 0) {
                throw new IllegalStateException(
                        "原物料 id=" + materialId + " 庫存不足，需要 " + needed + "，目前只有 " + total);
            }
        }

        // 第二步：確認都夠了，才開始依效期由近到遠（FIFO）扣減
        for (Bom bom : bomList) {

            Long materialId = bom.getMaterial().getId();
            BigDecimal needed = bom.getQuantity().multiply(saleQuantity);

            deductFifo(materialId, needed, refId);
        }
    }

    private BigDecimal getTotalQuantity(Long materialId) {

        List<Inventory> batches = inventoryRepository.findByMaterialIdOrderByExpiryDateAsc(materialId);

        BigDecimal total = BigDecimal.ZERO;

        for (Inventory batch : batches) {
            total = total.add(batch.getQuantity());
        }

        return total;
    }

    private void deductFifo(Long materialId, BigDecimal needed, Long refId) {

        List<Inventory> batches = inventoryRepository.findByMaterialIdOrderByExpiryDateAsc(materialId);

        BigDecimal remaining = needed;

        for (Inventory batch : batches) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal batchQty = batch.getQuantity();

            BigDecimal deductAmount = batchQty.compareTo(remaining) <= 0 ? batchQty : remaining;

            batch.setQuantity(batchQty.subtract(deductAmount));
            inventoryRepository.save(batch);

            InventoryLog log = new InventoryLog();
            log.setMaterial(batch.getMaterial());
            log.setQuantity(deductAmount.negate());
            log.setAction("SALE_DEDUCT");
            log.setRefId(refId);
            inventoryLogRepository.save(log);

            remaining = remaining.subtract(deductAmount);
        }
    }
}