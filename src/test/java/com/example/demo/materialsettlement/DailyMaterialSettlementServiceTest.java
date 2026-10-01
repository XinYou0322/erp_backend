package com.example.demo.materialsettlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.demo.analytics.OperationsAnalyticsService;
import com.example.demo.analytics.dto.MaterialUsageAnalysisResponse;
import com.example.demo.inventories.InventoryRepository;
import com.example.demo.inventories.InventoryService;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.materialsettlement.dto.SettlementPreviewResponse;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

class DailyMaterialSettlementServiceTest {

    private final DailyMaterialSettlementRepository settlementRepository =
            mock(DailyMaterialSettlementRepository.class);
    private final OperationsAnalyticsService analyticsService = mock(OperationsAnalyticsService.class);
    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final UsersRepository usersRepository = mock(UsersRepository.class);
    private final InventoryRepository inventoryRepository = mock(InventoryRepository.class);
    private final InventoryService inventoryService = mock(InventoryService.class);
    private final DailyMaterialSettlementService service = new DailyMaterialSettlementService(
            settlementRepository, analyticsService, materialRepository, usersRepository,
            inventoryRepository, inventoryService);

    @Test
    void previewShowsDifferenceButRequiresSavedDraftBeforeCompletion() {
        LocalDate date = LocalDate.of(2026, 9, 30);
        when(settlementRepository.findBySettlementDate(date)).thenReturn(Optional.empty());
        when(analyticsService.getMaterialUsageAnalysis(date)).thenReturn(List.of(usage()));
        when(inventoryRepository.existsByMaterialIdAndExpiryDateIsNotNull(1L)).thenReturn(true);

        SettlementPreviewResponse result = service.preview(date);

        assertEquals(1, result.getItems().size());
        assertEquals(0, bd("50").compareTo(result.getItems().get(0).getDifferenceQuantity()));
        assertEquals(0, bd("50").compareTo(result.getItems().get(0).getUnallocatedQuantity()));
        assertTrue(result.getItems().get(0).isReturnExpiryRequired());
        assertFalse(result.isCanComplete());
    }

    @Test
    void previewShowsCarryoverFromMostRecentCompletedSettlement() {
        LocalDate date = LocalDate.of(2026, 9, 30);
        Material material = material();
        DailyMaterialSettlement previous = new DailyMaterialSettlement();
        previous.setSettlementDate(date.minusDays(2));
        previous.setStatus(SettlementStatus.COMPLETED);
        DailyMaterialSettlementItem previousItem = new DailyMaterialSettlementItem();
        previousItem.setSettlement(previous);
        previousItem.setMaterial(material);
        previousItem.setWorkspaceCarryoverQuantity(bd("30"));
        previous.getItems().add(previousItem);

        when(settlementRepository.findBySettlementDate(date)).thenReturn(Optional.empty());
        when(settlementRepository
                .findFirstByStatusAndSettlementDateBeforeOrderBySettlementDateDesc(
                        SettlementStatus.COMPLETED, date))
                .thenReturn(Optional.of(previous));
        when(analyticsService.getMaterialUsageAnalysis(date)).thenReturn(List.of(usage()));

        SettlementPreviewResponse.Item result = service.preview(date).getItems().get(0);

        assertEquals(0, bd("30").compareTo(result.getPreviousCarryoverQuantity()));
        assertEquals(0, bd("130").compareTo(result.getWorkspaceAvailableQuantity()));
        assertEquals(0, bd("80").compareTo(result.getDifferenceQuantity()));
        assertEquals(0, bd("80").compareTo(result.getUnallocatedQuantity()));
    }

    @Test
    void previewKeepsCarryoverMaterialEvenWithoutNewManualIssue() {
        LocalDate date = LocalDate.of(2026, 9, 30);
        Material material = material();
        DailyMaterialSettlement previous = new DailyMaterialSettlement();
        previous.setSettlementDate(date.minusDays(1));
        previous.setStatus(SettlementStatus.COMPLETED);
        DailyMaterialSettlementItem previousItem = new DailyMaterialSettlementItem();
        previousItem.setSettlement(previous);
        previousItem.setMaterial(material);
        previousItem.setWorkspaceCarryoverQuantity(bd("30"));
        previous.getItems().add(previousItem);

        when(settlementRepository.findBySettlementDate(date)).thenReturn(Optional.empty());
        when(settlementRepository
                .findFirstByStatusAndSettlementDateBeforeOrderBySettlementDateDesc(
                        SettlementStatus.COMPLETED, date))
                .thenReturn(Optional.of(previous));
        when(analyticsService.getMaterialUsageAnalysis(date)).thenReturn(List.of());

        SettlementPreviewResponse.Item result = service.preview(date).getItems().get(0);

        assertEquals("MAT001", result.getMaterialCode());
        assertEquals(0, bd("30").compareTo(result.getPreviousCarryoverQuantity()));
        assertEquals(0, bd("30").compareTo(result.getDifferenceQuantity()));
    }

    @Test
    void completeReturnsAllocatedQuantityToInventoryAndLocksSettlement() {
        LocalDate date = LocalDate.of(2026, 9, 30);
        Material material = material();
        DailyMaterialSettlement settlement = new DailyMaterialSettlement();
        settlement.setId(10L);
        settlement.setSettlementDate(date);
        settlement.setStatus(SettlementStatus.DRAFT);
        User creator = mock(User.class);
        when(creator.getId()).thenReturn(3L);
        when(creator.getName()).thenReturn("測試人員");
        settlement.setCreatedBy(creator);

        DailyMaterialSettlementItem item = new DailyMaterialSettlementItem();
        item.setSettlement(settlement);
        item.setMaterial(material);
        item.setManualIssueQuantity(bd("100"));
        item.setTheoreticalUsageQuantity(bd("40"));
        item.setWasteQuantity(bd("10"));
        item.setUnrecordedUsageQuantity(BigDecimal.ZERO);
        item.setWorkspaceCarryoverQuantity(BigDecimal.ZERO);
        item.setReturnedQuantity(bd("50"));
        item.setReturnedExpiryDate(LocalDate.of(2026, 10, 15));
        settlement.getItems().add(item);

        when(settlementRepository.findByIdForCompletion(10L)).thenReturn(Optional.of(settlement));
        when(analyticsService.getMaterialUsageAnalysis(date)).thenReturn(List.of(usage()));
        when(inventoryRepository.existsByMaterialIdAndExpiryDateIsNotNull(1L)).thenReturn(true);
        when(settlementRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SettlementPreviewResponse result = service.complete(10L);

        verify(inventoryService).returnFromWorkspace(
                eq(material), eq(bd("50")), eq(LocalDate.of(2026, 10, 15)), eq(10L));
        assertEquals(SettlementStatus.COMPLETED, settlement.getStatus());
        assertEquals(SettlementStatus.COMPLETED, result.getStatus());
        assertFalse(result.isCanComplete());
    }

    private MaterialUsageAnalysisResponse usage() {
        return new MaterialUsageAnalysisResponse(
                1L, "MAT001", "阿薩姆紅茶葉", "g", "MANUAL",
                bd("100"), BigDecimal.ZERO, bd("40"), bd("40"), BigDecimal.ZERO,
                bd("10"), BigDecimal.ZERO, bd("50"), BigDecimal.ZERO, bd("50"),
                bd("125"), "HIGH", bd("50"), bd("50"), "請進行日結");
    }

    private Material material() {
        Material material = new Material();
        material.setId(1L);
        material.setCode("MAT001");
        material.setName("阿薩姆紅茶葉");
        material.setUnit("g");
        return material;
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
