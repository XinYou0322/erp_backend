package com.example.demo.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.analytics.dto.MaterialUsageAnalysisResponse;
import com.example.demo.analytics.dto.ReplenishmentSuggestionResponse;
import com.example.demo.inventorylog.InventoryLogRepository;
import com.example.demo.inventories.InventoryRepository;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.purchaseOrderItem.PurchaseOrderItemsRepository;
import com.example.demo.salesOrder.SalesOrderItemRepository;
import com.example.demo.salesOrder.SalesOrderStatus;

class OperationsAnalyticsServiceTest {

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final InventoryRepository inventoryRepository = mock(InventoryRepository.class);
    private final InventoryLogRepository inventoryLogRepository = mock(InventoryLogRepository.class);
    private final SalesOrderItemRepository salesOrderItemRepository = mock(SalesOrderItemRepository.class);
    private final PurchaseOrderItemsRepository purchaseOrderItemsRepository = mock(PurchaseOrderItemsRepository.class);
    private final OperationsAnalyticsService service = new OperationsAnalyticsService(
            materialRepository, inventoryRepository, inventoryLogRepository,
            salesOrderItemRepository, purchaseOrderItemsRepository);

    @Test
    void replenishmentSubtractsUsableStockAndApprovedPurchase() {
        Material material = material(1L, "MAT001", "紅茶", "g", "100");
        when(materialRepository.findByStatus("ACTIVE")).thenReturn(List.of(material));
        when(inventoryRepository.sumAvailableAndExpiredByMaterial(any()))
                .thenReturn(List.<Object[]>of(new Object[] { 1L, bd("40"), bd("5") }));
        when(salesOrderItemRepository.sumMaterialUsageBySales(eq(SalesOrderStatus.COMPLETED), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[] { 1L, "MAT001", "紅茶", "g", bd("70") }));
        when(purchaseOrderItemsRepository.sumApprovedQuantityByMaterial())
                .thenReturn(List.<Object[]>of(new Object[] { 1L, bd("20") }));

        ReplenishmentSuggestionResponse result = service
                .getReplenishmentSuggestions(LocalDate.of(2026, 9, 25), 7, 7).get(0);

        assertEquals(0, bd("10").compareTo(result.getAverageDailyUsage()));
        assertEquals(0, bd("110").compareTo(result.getSuggestedPurchaseQuantity()));
        assertEquals(0, bd("4").compareTo(result.getEstimatedDaysRemaining()));
        assertEquals("HIGH", result.getRiskLevel());
        assertEquals(0, bd("110").compareTo(result.getSuggestedPackageCount()));
    }

    @Test
    void manualModeComparesManualIssueWithNonAutoSalesUsage() {
        when(salesOrderItemRepository.sumMaterialUsageBySales(eq(SalesOrderStatus.COMPLETED), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[] { 1L, "MAT001", "紅茶", "g", bd("80") }));
        when(inventoryLogRepository.findAutoDeductSalesOrderIds(any(), any())).thenReturn(List.of());
        when(inventoryLogRepository.sumActionsByMaterial(anyList(), any(), any()))
                .thenReturn(List.<Object[]>of(
                        new Object[] { 1L, "MAT001", "紅茶", "g", "MANUAL_USE", bd("-100") },
                        new Object[] { 1L, "MAT001", "紅茶", "g", "WASTE", bd("-5") }));

        MaterialUsageAnalysisResponse result = service
                .getMaterialUsageAnalysis(LocalDate.of(2026, 9, 25)).get(0);

        assertEquals("MANUAL", result.getAnalysisMode());
        assertEquals(0, bd("100").compareTo(result.getManualIssueQuantity()));
        assertEquals(0, bd("85").compareTo(result.getAccountedUsageQuantity()));
        assertEquals(0, bd("15").compareTo(result.getEstimatedWorkspaceRemaining()));
        assertEquals(0, bd("15").compareTo(result.getManualVarianceQuantity()));
        assertEquals(0, bd("18.75").compareTo(result.getVarianceRate()));
        assertEquals("ATTENTION", result.getRiskLevel());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getAutoDeductQuantity()));
    }

    @Test
    void replenishmentRoundsUpToWholePurchasePackages() {
        Material material = material(1L, "MAT001", "紅茶", "g", "100");
        material.setLeadTimeDays(5);
        material.setPurchasePackQuantity(bd("2500"));
        when(materialRepository.findByStatus("ACTIVE")).thenReturn(List.of(material));
        when(inventoryRepository.sumAvailableAndExpiredByMaterial(any()))
                .thenReturn(List.<Object[]>of(new Object[] { 1L, bd("1000"), bd("0") }));
        when(salesOrderItemRepository.sumMaterialUsageBySales(eq(SalesOrderStatus.COMPLETED), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[] { 1L, "MAT001", "紅茶", "g", bd("7000") }));
        when(purchaseOrderItemsRepository.sumApprovedQuantityByMaterial()).thenReturn(List.of());

        ReplenishmentSuggestionResponse result = service
                .getReplenishmentSuggestions(LocalDate.of(2026, 9, 25), 7, 7).get(0);

        assertEquals(0, bd("2").compareTo(result.getSuggestedPackageCount()));
        assertEquals(0, bd("5000").compareTo(result.getSuggestedPurchaseQuantity()));
        assertEquals(5, result.getLeadTimeDays());
    }

    private Material material(Long id, String code, String name, String unit, String safetyStock) {
        Material value = new Material();
        value.setId(id);
        value.setCode(code);
        value.setName(name);
        value.setUnit(unit);
        value.setSafetyStock(bd(safetyStock));
        return value;
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
