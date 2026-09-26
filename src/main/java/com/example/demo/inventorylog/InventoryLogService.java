package com.example.demo.inventorylog;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;
import com.example.demo.inventories.Inventory;
import com.example.demo.inventories.InventoryRepository;
import com.example.demo.inventorylog.DTO.InventoryAdjustmentItemDTO;
import com.example.demo.inventorylog.DTO.InventoryAdjustmentRequestDTO;
import com.example.demo.inventorylog.DTO.InventoryLogResponseDTO;
import com.example.demo.materials.Material;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryLogService {

    // 【本次新增：銷售與庫存同步】
    // 使用固定 action 區分銷售扣除與報廢回補，並搭配 refId（銷售單 ID）防止重複處理。
    private static final String SALE_DEDUCT = "SALE_DEDUCT";
    private static final String SALE_RESTORE = "SALE_RESTORE";

    public final BomRepository bomRepository;
    public final InventoryRepository inventoryRepository;
    public final InventoryLogRepository inventoryLogRepository;

    // 【本次修改：銷售與庫存同步】
    // 保留原本單一商品扣庫存入口，內部改由共用流程處理，並保留來源單據 refId。
    @Transactional(rollbackFor = Exception.class)
    public void deduct(Long productId, BigDecimal saleQuantity, Long refId) {
        deductItems(List.of(new InventoryDeductionItem(productId, saleQuantity)), refId);
    }

    /**
     * 【本次新增：銷售與庫存同步】
     * 銷售單專用扣庫存入口。所有商品會先換算並合併 BOM 需求，再鎖定批次、
     * 一次檢查全部庫存；任一原物料不足時，由外層銷售交易一併回滾。
     */
    @Transactional(rollbackFor = Exception.class)
    public void deductSale(List<InventoryDeductionItem> items, Long salesOrderId) {
        if (salesOrderId == null) {
            throw new IllegalArgumentException("銷售單 ID 不得為空");
        }
        if (inventoryLogRepository.existsByActionAndRefId(SALE_DEDUCT, salesOrderId)) {
            throw new IllegalStateException("此銷售單已完成庫存扣除，不可重複扣庫存");
        }
        deductItems(items, salesOrderId);
    }

    // 【本次新增：銷售與庫存同步】
    // 共用扣庫存流程：驗證商品資料、展開 BOM，並合併同一原物料的總需求量。
    private void deductItems(List<InventoryDeductionItem> items, Long refId) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("銷售商品不得為空");
        }

        Map<Long, MaterialRequirement> requirements = new LinkedHashMap<>();

        for (InventoryDeductionItem item : items) {
            if (item == null || item.productId() == null) {
                throw new IllegalArgumentException("商品 ID 不得為空");
            }
            if (item.quantity() == null || item.quantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("銷售數量必須大於 0");
            }

            List<Bom> bomList = bomRepository.findByProductId(item.productId());

            if (bomList.isEmpty()) {
                throw new IllegalStateException("商品 id=" + item.productId() + " 尚未設定配方 (BOM)");
            }

            for (Bom bom : bomList) {
                if (bom.getMaterial() == null || bom.getMaterial().getId() == null
                        || bom.getQuantity() == null
                        || bom.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalStateException("商品 id=" + item.productId() + " 的 BOM 設定不完整");
                }

                Material material = bom.getMaterial();
                BigDecimal needed = bom.getQuantity().multiply(item.quantity());

                requirements.compute(material.getId(), (materialId, existing) -> {
                    if (existing == null) {
                        return new MaterialRequirement(material, needed);
                    }
                    existing.quantity = existing.quantity.add(needed);
                    return existing;
                });
            }
        }

        Map<Long, List<Inventory>> lockedBatches = new LinkedHashMap<>();

        // 【本次新增：銷售與庫存同步】
        // 先鎖定並檢查全部原物料，避免同時結帳造成超賣。
        // 在任何批次真正扣除前先完成全部檢查，庫存不足時不會留下部分扣除結果。
        for (Map.Entry<Long, MaterialRequirement> entry : requirements.entrySet()) {
            List<Inventory> batches = inventoryRepository
                    .findByMaterialIdOrderForStockWithLock(entry.getKey());
            lockedBatches.put(entry.getKey(), batches);

            BigDecimal total = batches.stream()
                    .map(Inventory::getQuantity)
                    .filter(quantity -> quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (total.compareTo(entry.getValue().quantity) < 0) {
                throw new IllegalStateException(
                        "原物料「" + entry.getValue().material.getName() + "」庫存不足，需要 "
                                + entry.getValue().quantity + "，目前只有 " + total);
            }
        }

        // 【本次新增：銷售與庫存同步】全部足夠後，才依效期由近到遠扣除。
        for (Map.Entry<Long, MaterialRequirement> entry : requirements.entrySet()) {
            deductFromBatches(
                    lockedBatches.get(entry.getKey()),
                    entry.getValue().quantity,
                    refId);
        }
    }

    // 【本次新增：銷售與庫存同步】
    // 將需求量逐批扣除；每一筆實際扣除都建立負數 SALE_DEDUCT 紀錄並保存批次 ID。
    private void deductFromBatches(List<Inventory> batches, BigDecimal needed, Long refId) {
        BigDecimal remaining = needed;

        for (Inventory batch : batches) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            if (!isUsableBatch(batch)) {
                continue;
            }

            BigDecimal batchQty = batch.getQuantity();
            if (batchQty == null || batchQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal deductAmount = batchQty.min(remaining);

            batch.setQuantity(batchQty.subtract(deductAmount));
            inventoryRepository.save(batch);

            InventoryLog log = new InventoryLog();
            log.setMaterial(batch.getMaterial());
            log.setQuantity(deductAmount.negate());
            log.setAction(SALE_DEDUCT);
            log.setRefId(refId);
            log.setInventoryBatchId(batch.getId());
            log.setNote("銷售完成自動扣庫存");
            inventoryLogRepository.save(log);

            remaining = remaining.subtract(deductAmount);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("庫存扣除失敗，剩餘未扣數量：" + remaining);
        }
    }

    /**
     * 【本次新增：銷售與庫存同步】
     * 回補指定銷售單曾實際扣除的批次。沒有 SALE_DEDUCT 紀錄代表建立銷售單時
     * 同步開關未開啟，維持既有報廢流程且不異動庫存。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreSale(Long salesOrderId) {
        if (salesOrderId == null) {
            throw new IllegalArgumentException("銷售單 ID 不得為空");
        }
        if (inventoryLogRepository.existsByActionAndRefId(SALE_RESTORE, salesOrderId)) {
            throw new IllegalStateException("此銷售單的庫存已回補，不可重複回補");
        }

        List<InventoryLog> deductionLogs = inventoryLogRepository
                .findByActionAndRefIdOrderByIdAsc(SALE_DEDUCT, salesOrderId);

        if (deductionLogs.isEmpty()) {
            return false;
        }

        for (InventoryLog deductionLog : deductionLogs) {
            BigDecimal deductedQuantity = deductionLog.getQuantity();
            if (deductedQuantity == null || deductedQuantity.compareTo(BigDecimal.ZERO) >= 0) {
                throw new IllegalStateException("銷售單的扣庫存紀錄異常，無法回補");
            }

            BigDecimal restoreQuantity = deductedQuantity.abs();
            Inventory batch = findOrCreateRestoreBatch(deductionLog);
            batch.setQuantity(batch.getQuantity().add(restoreQuantity));
            Inventory savedBatch = inventoryRepository.save(batch);

            InventoryLog restoreLog = new InventoryLog();
            restoreLog.setMaterial(deductionLog.getMaterial());
            restoreLog.setQuantity(restoreQuantity);
            restoreLog.setAction(SALE_RESTORE);
            restoreLog.setRefId(salesOrderId);
            restoreLog.setInventoryBatchId(savedBatch.getId());
            restoreLog.setNote("銷售單報廢回補庫存");
            inventoryLogRepository.save(restoreLog);
        }

        return true;
    }

    // 【本次新增：銷售與庫存同步】
    // 優先鎖定並回補原批次；若原批次已刪除，建立同原物料的替代批次，確保庫存數量不遺失。
    private Inventory findOrCreateRestoreBatch(InventoryLog deductionLog) {
        Long batchId = deductionLog.getInventoryBatchId();

        if (batchId != null) {
            Inventory existing = inventoryRepository.findByIdWithLock(batchId).orElse(null);
            if (existing != null) {
                if (!existing.getMaterial().getId().equals(deductionLog.getMaterial().getId())) {
                    throw new IllegalStateException("庫存批次與扣庫存紀錄的原物料不一致");
                }
                return existing;
            }
        }

        // 原批次若已被刪除，以相同原物料建立無效期回補批次，避免遺失數量。
        Inventory replacement = new Inventory();
        replacement.setMaterial(deductionLog.getMaterial());
        replacement.setQuantity(BigDecimal.ZERO);
        replacement.setExpiryDate(null);
        return replacement;
    }

    // 【本次新增：銷售與庫存同步】保存合併後的原物料需求，供一次性庫存檢查與扣除使用。
    private static final class MaterialRequirement {
        private final Material material;
        private BigDecimal quantity;

        private MaterialRequirement(Material material, BigDecimal quantity) {
            this.material = material;
            this.quantity = quantity;
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

