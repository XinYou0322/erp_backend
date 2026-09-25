package com.example.demo.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.analytics.dto.MaterialUsageAnalysisResponse;
import com.example.demo.analytics.dto.ReplenishmentSuggestionResponse;
import com.example.demo.inventorylog.InventoryLogRepository;
import com.example.demo.inventories.InventoryRepository;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.purchaseOrderItem.PurchaseOrderItemsRepository;
import com.example.demo.salesOrder.SalesOrderItemRepository;
import com.example.demo.salesOrder.SalesOrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationsAnalyticsService {

    private static final ZoneId TAIPEI = ZoneId.of("Asia/Taipei");
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final MaterialRepository materialRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final PurchaseOrderItemsRepository purchaseOrderItemsRepository;

    public List<ReplenishmentSuggestionResponse> getReplenishmentSuggestions(
            LocalDate date, int historyDays, int forecastDays) {
        LocalDate targetDate = date == null ? LocalDate.now(TAIPEI) : date;
        validateRange("historyDays", historyDays, 1, 90);
        validateRange("forecastDays", forecastDays, 1, 30);

        Map<Long, StockAmount> stockByMaterial = new HashMap<>();
        for (Object[] row : inventoryRepository.sumAvailableAndExpiredByMaterial(targetDate)) {
            stockByMaterial.put(asLong(row[0]), new StockAmount(decimal(row[1]), decimal(row[2])));
        }

        Map<Long, BigDecimal> usageByMaterial = quantitiesByMaterial(
                salesOrderItemRepository.sumMaterialUsageBySales(
                        SalesOrderStatus.COMPLETED,
                        targetDate.minusDays(historyDays - 1L).atStartOfDay(),
                        targetDate.plusDays(1).atStartOfDay()));
        Map<Long, BigDecimal> usage7DaysByMaterial = quantitiesByMaterial(
                salesOrderItemRepository.sumMaterialUsageBySales(
                        SalesOrderStatus.COMPLETED,
                        targetDate.minusDays(6).atStartOfDay(),
                        targetDate.plusDays(1).atStartOfDay()));
        Map<Long, BigDecimal> usage30DaysByMaterial = quantitiesByMaterial(
                salesOrderItemRepository.sumMaterialUsageBySales(
                        SalesOrderStatus.COMPLETED,
                        targetDate.minusDays(29).atStartOfDay(),
                        targetDate.plusDays(1).atStartOfDay()));
        Map<Long, BigDecimal> pendingByMaterial = quantitiesByMaterial(
                purchaseOrderItemsRepository.sumApprovedQuantityByMaterial());

        List<ReplenishmentSuggestionResponse> result = new ArrayList<>();
        for (Material material : materialRepository.findByStatus("ACTIVE")) {
            StockAmount stock = stockByMaterial.getOrDefault(material.getId(), StockAmount.ZERO);
            BigDecimal safetyStock = decimal(material.getSafetyStock());
            BigDecimal recentUsage = usageByMaterial.getOrDefault(material.getId(), BigDecimal.ZERO);
            BigDecimal averageDailyUsage = recentUsage.divide(
                    BigDecimal.valueOf(historyDays), 4, RoundingMode.HALF_UP);
            BigDecimal forecastUsage = averageDailyUsage
                    .multiply(BigDecimal.valueOf(forecastDays)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal pending = pendingByMaterial.getOrDefault(material.getId(), BigDecimal.ZERO);
            BigDecimal usage7Days = usage7DaysByMaterial.getOrDefault(material.getId(), BigDecimal.ZERO);
            BigDecimal average7Days = usage7Days.divide(BigDecimal.valueOf(7), 4, RoundingMode.HALF_UP);
            BigDecimal usage30Days = usage30DaysByMaterial.getOrDefault(material.getId(), BigDecimal.ZERO);
            BigDecimal average30Days = usage30Days.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
            BigDecimal selectedAverage = average7Days.max(average30Days);
            int leadTimeDays = material.getLeadTimeDays() == null ? 7 : material.getLeadTimeDays();
            BigDecimal leadTimeDemand = selectedAverage.multiply(BigDecimal.valueOf(leadTimeDays))
                    .setScale(4, RoundingMode.HALF_UP);
            BigDecimal packQuantity = material.getPurchasePackQuantity() == null
                    || material.getPurchasePackQuantity().signum() <= 0
                            ? BigDecimal.ONE
                            : material.getPurchasePackQuantity();
            BigDecimal rawSuggestion = leadTimeDemand.add(safetyStock)
                    .subtract(stock.available()).subtract(pending).max(BigDecimal.ZERO);
            BigDecimal suggestedPackages = rawSuggestion.signum() == 0
                    ? BigDecimal.ZERO
                    : rawSuggestion.divide(packQuantity, 0, RoundingMode.CEILING);
            BigDecimal suggested = suggestedPackages.multiply(packQuantity).setScale(4, RoundingMode.UNNECESSARY);
            BigDecimal daysRemaining = selectedAverage.signum() == 0
                    ? null
                    : stock.available().divide(selectedAverage, 2, RoundingMode.HALF_UP);
            String risk = riskLevel(stock.available(), daysRemaining, selectedAverage, leadTimeDays);

            result.add(new ReplenishmentSuggestionResponse(
                    material.getId(), material.getCode(), material.getName(), material.getUnit(),
                    stock.available(), stock.expired(), safetyStock, recentUsage,
                    averageDailyUsage, forecastUsage, pending, suggested, daysRemaining,
                    inventoryStatus(stock.available(), safetyStock), risk,
                    usage7Days, average7Days, usage30Days, average30Days, selectedAverage,
                    leadTimeDays, leadTimeDemand, packQuantity, suggestedPackages,
                    replenishmentRecommendation(material, stock.available(), daysRemaining,
                            leadTimeDays, suggestedPackages, suggested, risk)));
        }

        result.sort((left, right) -> {
            int byRisk = Integer.compare(riskRank(left.getRiskLevel()), riskRank(right.getRiskLevel()));
            return byRisk != 0 ? byRisk : left.getMaterialCode().compareTo(right.getMaterialCode());
        });
        return result;
    }

    public List<MaterialUsageAnalysisResponse> getMaterialUsageAnalysis(LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now(TAIPEI) : date;
        Instant logStart = targetDate.atStartOfDay(TAIPEI).toInstant();
        Instant logEnd = targetDate.plusDays(1).atStartOfDay(TAIPEI).toInstant();
        LocalDateTime salesStart = targetDate.atStartOfDay();
        LocalDateTime salesEnd = targetDate.plusDays(1).atStartOfDay();

        Map<Long, UsageRow> rows = new LinkedHashMap<>();
        List<Object[]> totalUsageRows = salesOrderItemRepository.sumMaterialUsageBySales(
                SalesOrderStatus.COMPLETED, salesStart, salesEnd);
        addTheoretical(rows, totalUsageRows, false);

        List<Long> autoOrderIds = inventoryLogRepository.findAutoDeductSalesOrderIds(logStart, logEnd);
        if (!autoOrderIds.isEmpty()) {
            addTheoretical(rows, salesOrderItemRepository.sumMaterialUsageBySalesOrderIds(
                    SalesOrderStatus.COMPLETED, autoOrderIds), true);
        }

        List<String> actions = List.of("MANUAL_USE", "SALE_DEDUCT", "WASTE", "EXPIRED");
        for (Object[] row : inventoryLogRepository.sumActionsByMaterial(actions, logStart, logEnd)) {
            UsageRow item = rows.computeIfAbsent(asLong(row[0]), ignored -> UsageRow.from(row));
            BigDecimal quantity = decimal(row[5]).abs();
            switch ((String) row[4]) {
                case "MANUAL_USE" -> item.manualIssue = quantity;
                case "SALE_DEDUCT" -> item.autoDeduct = quantity;
                case "WASTE" -> item.waste = quantity;
                case "EXPIRED" -> item.expired = quantity;
                default -> { }
            }
        }

        return rows.values().stream()
                .map(this::toUsageResponse)
                .sorted((left, right) -> left.getMaterialCode().compareTo(right.getMaterialCode()))
                .toList();
    }

    private void addTheoretical(Map<Long, UsageRow> rows, List<Object[]> source, boolean auto) {
        for (Object[] row : source) {
            UsageRow item = rows.computeIfAbsent(asLong(row[0]), ignored -> UsageRow.from(row));
            if (auto) {
                item.autoTheoretical = decimal(row[4]);
            } else {
                item.totalTheoretical = decimal(row[4]);
            }
        }
    }

    private MaterialUsageAnalysisResponse toUsageResponse(UsageRow row) {
        BigDecimal manualTheoretical = row.totalTheoretical.subtract(row.autoTheoretical).max(BigDecimal.ZERO);
        BigDecimal accountedUsage = manualTheoretical.add(row.waste);
        BigDecimal workspaceRemaining = row.manualIssue.subtract(accountedUsage);
        BigDecimal manualVariance = workspaceRemaining;
        BigDecimal autoVariance = row.autoDeduct.subtract(row.autoTheoretical);
        BigDecimal combinedVariance = manualVariance.add(autoVariance);
        String mode = row.autoDeduct.signum() > 0 || row.autoTheoretical.signum() > 0
                ? (row.manualIssue.signum() > 0 || manualTheoretical.signum() > 0 ? "MIXED" : "POS_AUTO")
                : "MANUAL";
        BigDecimal denominator = "POS_AUTO".equals(mode) ? row.autoTheoretical : row.totalTheoretical;
        BigDecimal rate = denominator.signum() == 0
                ? null
                : combinedVariance.multiply(ONE_HUNDRED).divide(denominator, 2, RoundingMode.HALF_UP);

        return new MaterialUsageAnalysisResponse(
                row.id, row.code, row.name, row.unit, mode,
                row.manualIssue, row.autoDeduct, row.totalTheoretical,
                manualTheoretical, row.autoTheoretical, row.waste, row.expired,
                manualVariance, autoVariance, combinedVariance, rate, varianceRisk(rate, combinedVariance),
                accountedUsage, workspaceRemaining,
                usageRecommendation(mode, workspaceRemaining, row.waste, manualTheoretical, autoVariance));
    }

    private Map<Long, BigDecimal> quantitiesByMaterial(List<Object[]> rows) {
        Map<Long, BigDecimal> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put(asLong(row[0]), decimal(row[row.length - 1]));
        }
        return result;
    }

    private void validateRange(String name, int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " 必須介於 " + min + " 到 " + max + " 之間");
        }
    }

    private BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : (BigDecimal) value;
    }

    private Long asLong(Object value) {
        return ((Number) value).longValue();
    }

    private String inventoryStatus(BigDecimal available, BigDecimal safety) {
        if (available.compareTo(safety.multiply(BigDecimal.valueOf(0.4))) <= 0) return "URGENT";
        if (available.compareTo(safety) <= 0) return "LOW";
        return "NORMAL";
    }

    private String riskLevel(BigDecimal available, BigDecimal days, BigDecimal averageUsage, int leadTimeDays) {
        if (averageUsage.signum() == 0) return "NO_RECENT_USAGE";
        if (available.signum() <= 0) return "OUT_OF_STOCK";
        if (days.compareTo(BigDecimal.valueOf(leadTimeDays)) < 0) return "HIGH";
        if (days.compareTo(BigDecimal.valueOf(leadTimeDays + 2L)) < 0) return "ATTENTION";
        return "NORMAL";
    }

    private String replenishmentRecommendation(Material material, BigDecimal available, BigDecimal days,
            int leadTimeDays, BigDecimal packages, BigDecimal suggested, String risk) {
        if ("NO_RECENT_USAGE".equals(risk)) {
            return material.getName() + "最近 30 天沒有銷售耗用，暫不建議依銷售量補貨。";
        }
        if (packages.signum() > 0) {
            return material.getName() + "目前可用庫存 " + available.stripTrailingZeros().toPlainString()
                    + " " + material.getUnit() + "，預估可使用 "
                    + (days == null ? "—" : days.stripTrailingZeros().toPlainString())
                    + " 天，交期為 " + leadTimeDays + " 天；建議採購 "
                    + packages.toPlainString() + " 個包裝，共 "
                    + suggested.stripTrailingZeros().toPlainString() + " " + material.getUnit() + "。";
        }
        return material.getName() + "目前庫存與已核准未到貨量足以涵蓋交期需求，暫不需要補貨。";
    }

    private String usageRecommendation(String mode, BigDecimal workspaceRemaining, BigDecimal waste,
            BigDecimal theoretical, BigDecimal autoVariance) {
        if ("POS_AUTO".equals(mode)) {
            return autoVariance.signum() == 0
                    ? "POS 扣料量與最新 BOM 理論耗用一致。"
                    : "POS 扣料量與最新 BOM 理論耗用不一致，建議檢查配方或扣料紀錄。";
        }
        if (workspaceRemaining.signum() < 0) {
            return "領料量不足以解釋理論耗用與已登記耗損，可能使用前期備料或有漏登領料，建議確認。";
        }
        if (theoretical.signum() > 0
                && waste.multiply(ONE_HUNDRED).divide(theoretical, 2, RoundingMode.HALF_UP)
                        .compareTo(BigDecimal.TEN) >= 0) {
            return "已登記耗損超過理論耗用的 10%，建議檢查製作流程。";
        }
        if (workspaceRemaining.signum() > 0) {
            return "領料扣除理論耗用與耗損後仍有推估剩餘，建議盤點工作區存量。";
        }
        return "領料、理論耗用與已登記耗損目前可互相對應。";
    }

    private int riskRank(String risk) {
        return switch (risk) {
            case "OUT_OF_STOCK" -> 0;
            case "HIGH" -> 1;
            case "ATTENTION" -> 2;
            case "NORMAL" -> 3;
            default -> 4;
        };
    }

    private String varianceRisk(BigDecimal rate, BigDecimal variance) {
        if (rate == null) return variance.signum() == 0 ? "NORMAL" : "REVIEW";
        BigDecimal absolute = rate.abs();
        if (absolute.compareTo(BigDecimal.valueOf(20)) >= 0) return "HIGH";
        if (absolute.compareTo(BigDecimal.valueOf(10)) >= 0) return "ATTENTION";
        return "NORMAL";
    }

    private record StockAmount(BigDecimal available, BigDecimal expired) {
        private static final StockAmount ZERO = new StockAmount(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private static class UsageRow {
        private Long id;
        private String code;
        private String name;
        private String unit;
        private BigDecimal manualIssue = BigDecimal.ZERO;
        private BigDecimal autoDeduct = BigDecimal.ZERO;
        private BigDecimal totalTheoretical = BigDecimal.ZERO;
        private BigDecimal autoTheoretical = BigDecimal.ZERO;
        private BigDecimal waste = BigDecimal.ZERO;
        private BigDecimal expired = BigDecimal.ZERO;

        private static UsageRow from(Object[] row) {
            UsageRow value = new UsageRow();
            value.id = ((Number) row[0]).longValue();
            value.code = (String) row[1];
            value.name = (String) row[2];
            value.unit = (String) row[3];
            return value;
        }
    }
}
