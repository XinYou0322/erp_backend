package com.example.demo.inventorylog;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;
import com.example.demo.inventories.Inventory;
import com.example.demo.inventories.InventoryRepository;
import com.example.demo.inventorylog.DTO.InventoryAdjustmentItemDTO;
import com.example.demo.inventorylog.DTO.InventoryAdjustmentRequestDTO;
import com.example.demo.inventorylog.DTO.InventoryLogResponseDTO;

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
            if (isUsableBatch(batch)) {
                total = total.add(batch.getQuantity());
            }
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

            if (!isUsableBatch(batch)) {
                continue;
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
            log.setNote("inventoryBatchId=" + batch.getId());
            inventoryLogRepository.save(log);

            remaining = remaining.subtract(deductAmount);
        }
    }

    private boolean isUsableBatch(Inventory batch) {
        return batch.getQuantity() != null
                && batch.getQuantity().compareTo(BigDecimal.ZERO) > 0
                && (batch.getExpiryDate() == null
                        || !batch.getExpiryDate().isBefore(LocalDate.now()));
    }

    @Transactional
    public void restoreSaleDeduction(Long salesOrderId) {
        List<InventoryLog> deductionLogs = inventoryLogRepository
                .findByRefIdAndActionOrderByIdAsc(salesOrderId, "SALE_DEDUCT");

        if (deductionLogs.isEmpty()) {
            return;
        }

        if (!inventoryLogRepository
                .findByRefIdAndActionOrderByIdAsc(salesOrderId, "SALE_RETURN")
                .isEmpty()) {
            throw new IllegalStateException("此銷售單的原物料已經回補");
        }

        for (InventoryLog deductionLog : deductionLogs) {
            Long batchId = parseBatchId(deductionLog.getNote());
            Inventory batch = inventoryRepository.findById(batchId)
                    .orElseThrow(() -> new IllegalStateException("找不到原銷售扣料批次：" + batchId));

            BigDecimal restoredQuantity = deductionLog.getQuantity().abs();
            batch.setQuantity(batch.getQuantity().add(restoredQuantity));
            inventoryRepository.save(batch);

            InventoryLog returnLog = new InventoryLog();
            returnLog.setMaterial(deductionLog.getMaterial());
            returnLog.setQuantity(restoredQuantity);
            returnLog.setAction("SALE_RETURN");
            returnLog.setRefId(salesOrderId);
            returnLog.setNote("inventoryBatchId=" + batchId);
            inventoryLogRepository.save(returnLog);
        }
    }

    private Long parseBatchId(String note) {
        String prefix = "inventoryBatchId=";
        if (note == null || !note.startsWith(prefix)) {
            throw new IllegalStateException("銷售扣料紀錄缺少庫存批次資訊");
        }
        try {
            return Long.valueOf(note.substring(prefix.length()));
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("銷售扣料紀錄的庫存批次資訊無效", exception);
        }
    }
        
    public List<InventoryLogResponseDTO> findAllLogs() {

        List<InventoryLog> logs =
                inventoryLogRepository
                        .findAllByOrderByCreatedAtDesc();

        return logs.stream()
                .map(this::convertToDTO)
                .toList();
    }
    public List<InventoryLogResponseDTO> findByMaterialId(
            Long materialId) {

        List<InventoryLog> logs =
                inventoryLogRepository
                        .findByMaterialIdOrderByCreatedAtDesc(
                                materialId
                        );

        return logs.stream()
                .map(this::convertToDTO)
                .toList();
    }
        
        
    private InventoryLogResponseDTO convertToDTO(
            InventoryLog log) {

        InventoryLogResponseDTO dto =
                new InventoryLogResponseDTO();

        dto.setId(log.getId());

        dto.setMaterialId(
                log.getMaterial().getId()
        );

        dto.setMaterialCode(
                log.getMaterial().getCode()
        );

        dto.setMaterialName(
                log.getMaterial().getName()
        );

        dto.setUnit(
                log.getMaterial().getUnit()
        );

        dto.setQuantity(
                log.getQuantity()
        );

        dto.setAction(
                log.getAction()
        );

        dto.setRefId(
                log.getRefId()
        );

        dto.setNote(
                log.getNote()
        );

        dto.setCreatedAt(
                log.getCreatedAt()
        );

        return dto;
    }
    
   
    public List<InventoryLogResponseDTO> getLogsByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        // 1. 檢查日期
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                "開始日期不得晚於結束日期"
            );
        }

        // 2. 使用台灣時區
        ZoneId zoneId = ZoneId.of("Asia/Taipei");

        // 3. 開始日期 00:00
        Instant start = startDate
            .atStartOfDay(zoneId)
            .toInstant();

        // 4. 結束日期的隔天 00:00
        Instant end = endDate
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant();

        // 5. 查詢資料
        List<InventoryLog> logs =
            inventoryLogRepository
                .findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                    start,
                    end
                );

        // 6. Entity → DTO
        return logs.stream()
            .map(this::convertToDTO)
            .toList();
    }
    
    
    @Transactional
    public void adjustInventory(
            InventoryAdjustmentRequestDTO request) {

        for (InventoryAdjustmentItemDTO item : request.getItems()) {

            Inventory inventory =
                    inventoryRepository
                            .findById(item.getInventoryId())
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "找不到庫存批次 id="
                                                    + item.getInventoryId()
                                    )
                            );


            BigDecimal quantity =
                    item.getQuantity();


            if (quantity == null
                    || quantity.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "異動數量必須大於 0"
                );
            }


            String action =
                    item.getAction();


            BigDecimal changeQuantity;


            if (
                "WASTE".equals(action)
                ||
                "EXPIRED".equals(action)
                ||
                "MANUAL_USE".equals(action)
            ) {

                changeQuantity =
                        quantity.negate();

            } else if (
                "ADJUSTMENT_IN".equals(action)
            ) {

                changeQuantity =
                        quantity;

            } else if (
                "ADJUSTMENT_OUT".equals(action)
            ) {

                changeQuantity =
                        quantity.negate();

            } else {

                throw new IllegalArgumentException(
                        "不支援的異動類型：" + action
                );
            }


            BigDecimal newQuantity =
                    inventory.getQuantity()
                            .add(changeQuantity);


            if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {

                throw new IllegalStateException(
                        inventory.getMaterial().getName()
                                + " 庫存不足"
                );
            }


            inventory.setQuantity(
                    newQuantity
            );


            inventoryRepository.save(
                    inventory
            );


            InventoryLog log =
                    new InventoryLog();

            log.setMaterial(
                    inventory.getMaterial()
            );

            log.setQuantity(
                    changeQuantity
            );

            log.setAction(
                    action
            );

            log.setNote(
                    item.getNote()
            );

            inventoryLogRepository.save(
                    log
            );
        }
        
        
    }
        
        
        
        
        
        
    
}
