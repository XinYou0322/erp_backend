package com.example.demo.materialsettlement;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.analytics.OperationsAnalyticsService;
import com.example.demo.analytics.dto.MaterialUsageAnalysisResponse;
import com.example.demo.inventories.InventoryRepository;
import com.example.demo.inventories.InventoryService;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.materialsettlement.dto.SaveSettlementRequest;
import com.example.demo.materialsettlement.dto.SettlementPreviewResponse;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyMaterialSettlementService {

    private static final ZoneId TAIPEI = ZoneId.of("Asia/Taipei");

    private final DailyMaterialSettlementRepository settlementRepository;
    private final OperationsAnalyticsService analyticsService;
    private final MaterialRepository materialRepository;
    private final UsersRepository usersRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    @Transactional(readOnly = true)
    public SettlementPreviewResponse preview(LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now(TAIPEI) : date;
        DailyMaterialSettlement settlement = settlementRepository
                .findBySettlementDate(targetDate)
                .orElse(null);
        return buildPreview(targetDate, settlement, usageByMaterial(targetDate));
    }

    @Transactional
    public SettlementPreviewResponse saveDraft(
            LocalDate date, SaveSettlementRequest request, Long userId) {
        if (date == null) throw new IllegalArgumentException("結算日期不得為空");
        if (date.isAfter(LocalDate.now(TAIPEI))) {
            throw new IllegalArgumentException("不可建立未來日期的領料結算");
        }
        if (request == null) throw new IllegalArgumentException("結算內容不得為空");

        Map<Long, MaterialUsageAnalysisResponse> usage = usageByMaterial(date);
        if (usage.isEmpty()) throw new IllegalStateException("當日沒有可結算的手動領料資料");

        DailyMaterialSettlement settlement = settlementRepository
                .findBySettlementDate(date)
                .orElseGet(DailyMaterialSettlement::new);
        if (settlement.getStatus() == SettlementStatus.COMPLETED) {
            throw new IllegalStateException("當日領料結算已完成，不可修改");
        }

        if (settlement.getId() == null) {
            User creator = usersRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("找不到結算操作人員"));
            settlement.setSettlementDate(date);
            settlement.setStatus(SettlementStatus.DRAFT);
            settlement.setCreatedBy(creator);
        }
        settlement.setNote(trimToNull(request.getNote()));

        Map<Long, SaveSettlementRequest.Item> requestedItems = requestItemsByMaterial(request);
        Set<Long> unknownMaterialIds = new HashSet<>(requestedItems.keySet());
        unknownMaterialIds.removeAll(usage.keySet());
        if (!unknownMaterialIds.isEmpty()) {
            throw new IllegalArgumentException("結算內容包含非當日手動領料原物料：" + unknownMaterialIds);
        }

        Map<Long, Material> materials = new HashMap<>();
        materialRepository.findAllById(usage.keySet())
                .forEach(material -> materials.put(material.getId(), material));

        Map<Long, DailyMaterialSettlementItem> existingItems = new HashMap<>();
        Map<Long, BigDecimal> previousCarryover = previousCarryoverByMaterial(date);
        settlement.getItems().forEach(item ->
                existingItems.put(item.getMaterial().getId(), item));
        List<DailyMaterialSettlementItem> items = new ArrayList<>();
        for (MaterialUsageAnalysisResponse source : usage.values()) {
            Material material = materials.get(source.getMaterialId());
            if (material == null) {
                throw new IllegalStateException("找不到原物料：" + source.getMaterialCode());
            }
            SaveSettlementRequest.Item input = requestedItems.get(source.getMaterialId());
            DailyMaterialSettlementItem item = existingItems.getOrDefault(
                    source.getMaterialId(), new DailyMaterialSettlementItem());
            item.setSettlement(settlement);
            item.setMaterial(material);
            applyUsageSnapshot(item, source, previousCarryover.getOrDefault(
                    source.getMaterialId(), BigDecimal.ZERO));
            applyUserInput(item, input);
            validateInput(item, isReturnExpiryRequired(material.getId()));
            items.add(item);
        }

        settlement.getItems().removeIf(item -> !items.contains(item));
        for (DailyMaterialSettlementItem item : items) {
            if (!settlement.getItems().contains(item)) settlement.getItems().add(item);
        }
        DailyMaterialSettlement saved = settlementRepository.saveAndFlush(settlement);
        return buildPreview(date, saved, usage);
    }

    @Transactional
    public SettlementPreviewResponse complete(Long settlementId) {
        DailyMaterialSettlement settlement = settlementRepository
                .findByIdForCompletion(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("找不到當日領料結算"));
        if (settlement.getStatus() == SettlementStatus.COMPLETED) {
            throw new IllegalStateException("當日領料結算已完成，不可重複完成");
        }

        Map<Long, MaterialUsageAnalysisResponse> usage = usageByMaterial(settlement.getSettlementDate());
        Set<Long> savedMaterialIds = new HashSet<>();
        for (DailyMaterialSettlementItem item : settlement.getItems()) {
            savedMaterialIds.add(item.getMaterial().getId());
        }
        if (!savedMaterialIds.equals(usage.keySet())) {
            throw new IllegalStateException("當日領料資料已有異動，請重新儲存結算草稿後再完成");
        }

        for (DailyMaterialSettlementItem item : settlement.getItems()) {
            MaterialUsageAnalysisResponse source = usage.get(item.getMaterial().getId());
            applyUsageSnapshot(item, source, item.getPreviousCarryoverQuantity());
            boolean expiryRequired = isReturnExpiryRequired(item.getMaterial().getId());
            validateInput(item, expiryRequired);
            SettlementPreviewResponse.Item previewItem = toPreviewItem(
                    item, item.getPreviousCarryoverQuantity(), expiryRequired);
            if (!previewItem.isValid()) {
                throw new IllegalStateException(
                        item.getMaterial().getName() + "：" + previewItem.getValidationMessage());
            }
        }

        for (DailyMaterialSettlementItem item : settlement.getItems()) {
            if (positive(item.getReturnedQuantity())) {
                inventoryService.returnFromWorkspace(
                        item.getMaterial(), item.getReturnedQuantity(),
                        item.getReturnedExpiryDate(), settlement.getId());
            }
        }

        settlement.setStatus(SettlementStatus.COMPLETED);
        settlement.setCompletedAt(Instant.now());
        settlementRepository.saveAndFlush(settlement);
        return buildPreview(settlement.getSettlementDate(), settlement, usage);
    }

    private Map<Long, MaterialUsageAnalysisResponse> usageByMaterial(LocalDate date) {
        Map<Long, MaterialUsageAnalysisResponse> result = new LinkedHashMap<>();
        Map<Long, BigDecimal> previousCarryover = previousCarryoverByMaterial(date);
        for (MaterialUsageAnalysisResponse item : analyticsService.getMaterialUsageAnalysis(date)) {
            if (positive(item.getManualIssueQuantity())
                    || previousCarryover.containsKey(item.getMaterialId())) {
                result.put(item.getMaterialId(), item);
            }
        }
        settlementRepository
                .findFirstByStatusAndSettlementDateBeforeOrderBySettlementDateDesc(
                        SettlementStatus.COMPLETED, date)
                .ifPresent(previous -> previous.getItems().forEach(previousItem -> {
                    if (!positive(previousItem.getWorkspaceCarryoverQuantity())) return;
                    Material material = previousItem.getMaterial();
                    result.computeIfAbsent(material.getId(), ignored ->
                            carryoverOnlyUsage(material));
                }));
        return result;
    }

    private MaterialUsageAnalysisResponse carryoverOnlyUsage(Material material) {
        return new MaterialUsageAnalysisResponse(
                material.getId(), material.getCode(), material.getName(), material.getUnit(),
                "CARRYOVER", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                "NORMAL", BigDecimal.ZERO, BigDecimal.ZERO, "前次工作區留存待結算");
    }

    private Map<Long, SaveSettlementRequest.Item> requestItemsByMaterial(
            SaveSettlementRequest request) {
        Map<Long, SaveSettlementRequest.Item> result = new HashMap<>();
        if (request.getItems() == null) return result;
        for (SaveSettlementRequest.Item item : request.getItems()) {
            if (item == null || item.getMaterialId() == null) {
                throw new IllegalArgumentException("結算原物料不得為空");
            }
            if (result.put(item.getMaterialId(), item) != null) {
                throw new IllegalArgumentException("同一項原物料不可重複提交");
            }
        }
        return result;
    }

    private void applyUsageSnapshot(
            DailyMaterialSettlementItem target,
            MaterialUsageAnalysisResponse source,
            BigDecimal previousCarryover) {
        target.setPreviousCarryoverQuantity(zero(previousCarryover));
        target.setManualIssueQuantity(zero(source.getManualIssueQuantity()));
        target.setTheoreticalUsageQuantity(zero(source.getManualTheoreticalUsageQuantity()));
        target.setWasteQuantity(zero(source.getWasteQuantity()));
    }

    private void applyUserInput(
            DailyMaterialSettlementItem target,
            SaveSettlementRequest.Item input) {
        if (input == null) {
            target.setUnrecordedUsageQuantity(BigDecimal.ZERO);
            target.setWorkspaceCarryoverQuantity(BigDecimal.ZERO);
            target.setReturnedQuantity(BigDecimal.ZERO);
            return;
        }
        target.setUnrecordedUsageQuantity(zero(input.getUnrecordedUsageQuantity()));
        target.setUnrecordedReason(input.getUnrecordedReason());
        target.setUnrecordedNote(trimToNull(input.getUnrecordedNote()));
        target.setWorkspaceCarryoverQuantity(zero(input.getWorkspaceCarryoverQuantity()));
        target.setReturnedQuantity(zero(input.getReturnedQuantity()));
        target.setReturnedExpiryDate(input.getReturnedExpiryDate());
    }

    private void validateInput(DailyMaterialSettlementItem item, boolean expiryRequired) {
        requireNonNegative(item.getUnrecordedUsageQuantity(), "未記錄耗用量");
        requireNonNegative(item.getWorkspaceCarryoverQuantity(), "工作區留存量");
        requireNonNegative(item.getReturnedQuantity(), "退料入庫量");

        if (positive(item.getUnrecordedUsageQuantity()) && item.getUnrecordedReason() == null) {
            throw new IllegalArgumentException(item.getMaterial().getName() + "的未記錄耗用原因必填");
        }
        if (positive(item.getUnrecordedUsageQuantity())
                && item.getUnrecordedReason() == UnrecordedUsageReason.OTHER
                && item.getUnrecordedNote() == null) {
            throw new IllegalArgumentException(item.getMaterial().getName() + "選擇其他原因時必須填寫備註");
        }
        if (positive(item.getReturnedQuantity()) && expiryRequired
                && item.getReturnedExpiryDate() == null) {
            throw new IllegalArgumentException(item.getMaterial().getName() + "退料時必須填寫有效日期");
        }
        if (item.getReturnedExpiryDate() != null
                && item.getReturnedExpiryDate().isBefore(item.getSettlement().getSettlementDate())) {
            throw new IllegalArgumentException(item.getMaterial().getName() + "的退料有效日期不可早於結算日");
        }
    }

    private SettlementPreviewResponse buildPreview(
            LocalDate date,
            DailyMaterialSettlement settlement,
            Map<Long, MaterialUsageAnalysisResponse> usage) {
        Map<Long, BigDecimal> previousCarryover = previousCarryoverByMaterial(date);
        Map<Long, DailyMaterialSettlementItem> savedItems = new HashMap<>();
        if (settlement != null) {
            settlement.getItems().forEach(item -> savedItems.put(item.getMaterial().getId(), item));
        }

        List<SettlementPreviewResponse.Item> items = new ArrayList<>();
        for (MaterialUsageAnalysisResponse source : usage.values()) {
            DailyMaterialSettlementItem saved = savedItems.get(source.getMaterialId());
            BigDecimal carryover = saved == null
                    ? previousCarryover.getOrDefault(source.getMaterialId(), BigDecimal.ZERO)
                    : zero(saved.getPreviousCarryoverQuantity());
            if (saved == null) {
                items.add(toUnsavedPreviewItem(source, carryover));
            } else {
                items.add(toPreviewItem(
                        saved, carryover, isReturnExpiryRequired(source.getMaterialId())));
            }
        }

        boolean completed = settlement != null && settlement.getStatus() == SettlementStatus.COMPLETED;
        boolean containsAllSavedMaterials = settlement != null
                && savedItems.keySet().equals(usage.keySet());
        boolean canComplete = !completed && containsAllSavedMaterials && !items.isEmpty()
                && items.stream().allMatch(SettlementPreviewResponse.Item::isValid);
        return new SettlementPreviewResponse(
                settlement == null ? null : settlement.getId(),
                date,
                settlement == null ? SettlementStatus.DRAFT : settlement.getStatus(),
                settlement == null ? null : settlement.getNote(),
                settlement == null ? null : settlement.getCreatedBy().getId(),
                settlement == null ? null : settlement.getCreatedBy().getName(),
                settlement == null ? null : settlement.getCompletedAt(),
                canComplete,
                items);
    }

    private SettlementPreviewResponse.Item toUnsavedPreviewItem(
            MaterialUsageAnalysisResponse source, BigDecimal previousCarryover) {
        BigDecimal difference = difference(
                previousCarryover, source.getManualIssueQuantity(),
                source.getManualTheoreticalUsageQuantity(), source.getWasteQuantity());
        boolean valid = difference.signum() == 0;
        String message = difference.signum() < 0
                ? "理論耗用與報廢量超過現場可用量，請先確認紀錄"
                : (valid ? null : "尚有差距未分配");
        return new SettlementPreviewResponse.Item(
                source.getMaterialId(), source.getMaterialCode(), source.getMaterialName(), source.getUnit(),
                previousCarryover, previousCarryover.add(zero(source.getManualIssueQuantity())),
                zero(source.getManualIssueQuantity()), zero(source.getManualTheoreticalUsageQuantity()),
                zero(source.getWasteQuantity()), difference,
                BigDecimal.ZERO, null, null, BigDecimal.ZERO, BigDecimal.ZERO, null,
                difference, isReturnExpiryRequired(source.getMaterialId()), valid, message);
    }

    private SettlementPreviewResponse.Item toPreviewItem(
            DailyMaterialSettlementItem item, BigDecimal previousCarryover, boolean expiryRequired) {
        BigDecimal difference = difference(
                previousCarryover, item.getManualIssueQuantity(),
                item.getTheoreticalUsageQuantity(), item.getWasteQuantity());
        BigDecimal allocated = zero(item.getUnrecordedUsageQuantity())
                .add(zero(item.getWorkspaceCarryoverQuantity()))
                .add(zero(item.getReturnedQuantity()));
        BigDecimal unallocated = difference.subtract(allocated);
        String message = validationMessage(item, difference, unallocated, expiryRequired);
        return new SettlementPreviewResponse.Item(
                item.getMaterial().getId(), item.getMaterial().getCode(), item.getMaterial().getName(),
                item.getMaterial().getUnit(), previousCarryover,
                previousCarryover.add(zero(item.getManualIssueQuantity())), item.getManualIssueQuantity(),
                item.getTheoreticalUsageQuantity(), item.getWasteQuantity(), difference,
                item.getUnrecordedUsageQuantity(), item.getUnrecordedReason(), item.getUnrecordedNote(),
                item.getWorkspaceCarryoverQuantity(), item.getReturnedQuantity(),
                item.getReturnedExpiryDate(), unallocated, expiryRequired,
                message == null, message);
    }

    private Map<Long, BigDecimal> previousCarryoverByMaterial(LocalDate date) {
        Map<Long, BigDecimal> result = new HashMap<>();
        settlementRepository
                .findFirstByStatusAndSettlementDateBeforeOrderBySettlementDateDesc(
                        SettlementStatus.COMPLETED, date)
                .ifPresent(previous -> previous.getItems().forEach(item -> {
                    if (positive(item.getWorkspaceCarryoverQuantity())) {
                        result.put(item.getMaterial().getId(), item.getWorkspaceCarryoverQuantity());
                    }
                }));
        return result;
    }

    private String validationMessage(
            DailyMaterialSettlementItem item,
            BigDecimal difference,
            BigDecimal unallocated,
            boolean expiryRequired) {
        if (difference.signum() < 0) return "理論耗用與報廢量超過現場可用量，請先確認紀錄";
        if (unallocated.signum() > 0) return "尚有差距未分配";
        if (unallocated.signum() < 0) return "分配數量超過待處理差距";
        if (positive(item.getUnrecordedUsageQuantity()) && item.getUnrecordedReason() == null) {
            return "未記錄耗用原因必填";
        }
        if (positive(item.getUnrecordedUsageQuantity())
                && item.getUnrecordedReason() == UnrecordedUsageReason.OTHER
                && trimToNull(item.getUnrecordedNote()) == null) {
            return "選擇其他原因時必須填寫備註";
        }
        if (positive(item.getReturnedQuantity()) && expiryRequired
                && item.getReturnedExpiryDate() == null) {
            return "退料時必須填寫有效日期";
        }
        return null;
    }

    private boolean isReturnExpiryRequired(Long materialId) {
        return inventoryRepository.existsByMaterialIdAndExpiryDateIsNotNull(materialId);
    }

    private BigDecimal difference(
            BigDecimal previousCarryover,
            BigDecimal issue,
            BigDecimal theoretical,
            BigDecimal waste) {
        return zero(previousCarryover).add(zero(issue))
                .subtract(zero(theoretical)).subtract(zero(waste));
    }

    private BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean positive(BigDecimal value) {
        return value != null && value.signum() > 0;
    }

    private void requireNonNegative(BigDecimal value, String field) {
        if (value != null && value.signum() < 0) {
            throw new IllegalArgumentException(field + "不可小於 0");
        }
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
