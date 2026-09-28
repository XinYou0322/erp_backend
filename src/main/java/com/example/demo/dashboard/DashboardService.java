package com.example.demo.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dashboard.dto.CostDetailResponse;
import com.example.demo.dashboard.dto.DashboardResponse;
import com.example.demo.dashboard.dto.HourlySalesResponse;
import com.example.demo.dashboard.dto.MaterialConsumptionResponse;
import com.example.demo.dashboard.dto.OrderValueAnalysisRequest;
import com.example.demo.dashboard.dto.OrderValueAnalysisResponse;
import com.example.demo.dashboard.dto.RevenueDetailRequest;
import com.example.demo.dashboard.dto.RevenueDetailResponse;
import com.example.demo.dashboard.dto.RevenueTrendResponse;
import com.example.demo.dashboard.dto.TopProductResponse;
import com.example.demo.dashboard.dto.TopRevenueResponse;
import com.example.demo.salesOrder.SalesOrderItemRepository;
import com.example.demo.salesOrder.SalesOrderRepository;
import com.example.demo.salesOrder.SalesOrderStatus;
import com.example.demo.inventorylog.InventoryLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final SalesOrderRepository salesRepo;
        private final SalesOrderItemRepository itemRepo;
        private final InventoryLogRepository inventoryLogRepo;

        public DashboardResponse getDashboard() {

                LocalDateTime now = LocalDateTime.now();
                LocalDate today = now.toLocalDate();
                LocalDate yesterday = today.minusDays(1);

                DashboardResponse response = new DashboardResponse();

                // 1. 今日至今 (Today to Date): 今日 00:00 ~ 現在
                LocalDateTime todayStart = today.atStartOfDay();
                LocalDateTime todayEnd = now;

                BigDecimal todayRevenue = salesRepo.getRevenueBetween(todayStart, todayEnd);
                response.setTodayRevenue(todayRevenue);

                // 2. 昨日至今 (Yesterday to Date): 昨日 00:00 ~ 昨日現在
                LocalDateTime yesterdayStart = yesterday.atStartOfDay();
                LocalDateTime yesterdayEnd = now.minusDays(1);

                BigDecimal yesterdaySamePeriod = salesRepo.getRevenueBetween(yesterdayStart, yesterdayEnd);

                // 3. 計算環比 (成長率)
                response.setRevenueChangeRate(calcChangeRate(todayRevenue, yesterdaySamePeriod));

                // 4. 今日至今訂單數
                response.setTodayOrders(salesRepo.countOrdersBetween(todayStart, todayEnd));

                // 5. 今日至今平均客單價
                Double avg = salesRepo.getAverageOrderBetween(todayStart, todayEnd);
                response.setAverageOrderAmount(avg != null ? BigDecimal.valueOf(avg) : BigDecimal.ZERO);

                // 6. 今日至今成本 + 較昨日同期（區間與營收完全一致）
                BigDecimal todayCost = nullToZero(itemRepo.sumCostBetween(todayStart, todayEnd));
                BigDecimal yesterdayCostSamePeriod = nullToZero(itemRepo.sumCostBetween(yesterdayStart, yesterdayEnd));

                response.setTodayCost(todayCost);
                response.setCostChangeRate(calcChangeRate(todayCost, yesterdayCostSamePeriod));
                response.setTodayMissingCostCount(itemRepo.countItemsMissingCost(todayStart, todayEnd));
                
                LocalDateTime weekStart = today.minusDays(6).atStartOfDay(); // 包含今天共7天
                LocalDateTime weekEnd = today.atTime(LocalTime.MAX);
                LocalDateTime startDateForRecent = today.minusDays(6).atStartOfDay();

                // 七天營收趨勢
                List<RevenueTrendResponse> rawTrend = salesRepo.getWeeklyRevenue(weekStart, weekEnd).stream()
                                .map(r -> new RevenueTrendResponse(((java.sql.Date) r[0]).toLocalDate(),
                                                (BigDecimal) r[1]))
                                .toList();

                Map<LocalDate, BigDecimal> revenueByDate = rawTrend.stream()
                                .collect(Collectors.toMap(RevenueTrendResponse::getDate,
                                                RevenueTrendResponse::getRevenue));

                List<RevenueTrendResponse> trend = IntStream.range(0, 7)
                                .mapToObj(i -> {
                                        LocalDate date = today.minusDays(6 - i);
                                        BigDecimal revenue = revenueByDate.getOrDefault(date, BigDecimal.ZERO);
                                        return new RevenueTrendResponse(date, revenue);
                                })
                                .toList();

                // 熱門商品 Top5 (全部 & 近七天)
                List<TopProductResponse> top = itemRepo.findTopProducts(PageRequest.of(0, 5)).stream()
                                .map(r -> new TopProductResponse((String) r[0], (BigDecimal) r[1])).toList();

                List<TopProductResponse> recentTop = itemRepo
                                .findTopProductsSince(startDateForRecent, PageRequest.of(0, 5)).stream()
                                .map(r -> new TopProductResponse((String) r[0], (BigDecimal) r[1])).toList();

                // 營收排行 Top5
                List<TopRevenueResponse> topRevenue = itemRepo
                                .findTopProductsByRevenue(SalesOrderStatus.COMPLETED, PageRequest.of(0, 5)).stream()
                                .map(r -> new TopRevenueResponse((String) r[0], (BigDecimal) r[1])).toList();

                List<TopRevenueResponse> recentTopRevenue = itemRepo
                                .findTopProductsByRevenueSince(SalesOrderStatus.COMPLETED, weekStart,
                                                PageRequest.of(0, 5))
                                .stream()
                                .map(r -> new TopRevenueResponse((String) r[0], (BigDecimal) r[1])).toList();

                // 每小時銷售
                List<HourlySalesResponse> hourlySales = itemRepo
                                .findHourlySales(SalesOrderStatus.COMPLETED.name(), today).stream()
                                .map(r -> new HourlySalesResponse(((Number) r[0]).intValue(), (BigDecimal) r[1]))
                                .toList();

                response.setWeeklyRevenue(trend);
                response.setTopProducts(top);
                response.setRecentTopProducts(recentTop);
                response.setTopRevenueProducts(topRevenue);
                response.setRecentTopRevenueProducts(recentTopRevenue);
                response.setHourlySales(hourlySales);
                response.setMaterialConsumption(getMaterialConsumption(today));

                return response;
        }

        public List<MaterialConsumptionResponse> getMaterialConsumption(LocalDate date) {
                LocalDate targetDate = date == null ? LocalDate.now(ZoneId.of("Asia/Taipei")) : date;
                ZoneId taipei = ZoneId.of("Asia/Taipei");
                Instant logStart = targetDate.atStartOfDay(taipei).toInstant();
                Instant logEnd = targetDate.plusDays(1).atStartOfDay(taipei).toInstant();
                LocalDateTime salesStart = targetDate.atStartOfDay();
                LocalDateTime salesEnd = targetDate.plusDays(1).atStartOfDay();

                Map<Long, MaterialConsumptionResponse> result = new LinkedHashMap<>();

                for (Object[] row : inventoryLogRepo.sumManualUseByMaterial(logStart, logEnd)) {
                        BigDecimal signedQuantity = (BigDecimal) row[4];
                        MaterialConsumptionResponse item = new MaterialConsumptionResponse(
                                        (Long) row[0],
                                        (String) row[1],
                                        (String) row[2],
                                        (String) row[3],
                                        signedQuantity == null ? BigDecimal.ZERO : signedQuantity.abs(),
                                        BigDecimal.ZERO,
                                        BigDecimal.ZERO);
                        result.put(item.getMaterialId(), item);
                }

                for (Object[] row : itemRepo.sumMaterialUsageBySales(
                                SalesOrderStatus.COMPLETED, salesStart, salesEnd)) {
                        Long materialId = (Long) row[0];
                        MaterialConsumptionResponse item = result.computeIfAbsent(
                                        materialId,
                                        ignored -> new MaterialConsumptionResponse(
                                                        materialId,
                                                        (String) row[1],
                                                        (String) row[2],
                                                        (String) row[3],
                                                        BigDecimal.ZERO,
                                                        BigDecimal.ZERO,
                                                        BigDecimal.ZERO));
                        item.setTheoreticalUsageQuantity(
                                        row[4] == null ? BigDecimal.ZERO : (BigDecimal) row[4]);
                }

                return result.values().stream()
                                .peek(item -> item.setVarianceQuantity(
                                                item.getManualIssueQuantity()
                                                                .subtract(item.getTheoreticalUsageQuantity())))
                                .sorted((left, right) -> left.getMaterialCode().compareTo(right.getMaterialCode()))
                                .toList();
        }

        public RevenueDetailResponse getRevenueDetail(RevenueDetailRequest request) {

                LocalDate startDate = request.getStartDate();
                LocalDate endDate = request.getEndDate();
                String groupBy = request.getGroupBy(); // "DAY", "MONTH", "YEAR"

                if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
                        throw new IllegalArgumentException("無效的日期區間：開始日期不能晚於結束日期");
                }

                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.atTime(LocalTime.MAX);

                // 1. 計算「上期」區間：長度與本期相同，且緊接在本期開始之前
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
                LocalDateTime prevEnd = start.minusNanos(1); // 本期開始的前一瞬間
                LocalDateTime prevStart = prevEnd.minusDays(daysBetween).toLocalDate().atStartOfDay();

                // 2. 取得本期與上期總營收
                BigDecimal totalRevenue = salesRepo.getRevenueBetween(start, end);
                BigDecimal previousRevenue = salesRepo.getRevenueBetween(prevStart, prevEnd);

                // 3. 計算環比百分比
                Double changeRate = calcChangeRate(totalRevenue, previousRevenue);

                // 4. 根據分組維度取得原始資料
                List<Object[]> rawTrend;
                if ("MONTH".equals(groupBy)) {
                        rawTrend = salesRepo.getRevenueGroupedByMonth(start, end);
                } else if ("YEAR".equals(groupBy)) {
                        rawTrend = salesRepo.getRevenueGroupedByYear(start, end);
                } else {
                        rawTrend = salesRepo.getRevenueGroupedByDay(start, end);
                }

                // 5. 將資料轉換為 Map，並將年/月統一轉換為 LocalDate (年初或月初) 以便複用 RevenueTrendResponse
                Map<LocalDate, BigDecimal> revenueMap = rawTrend.stream()
                                .collect(Collectors.toMap(
                                                r -> {
                                                        if ("YEAR".equals(groupBy)) {
                                                                int year = ((Number) r[0]).intValue();
                                                                return LocalDate.of(year, 1, 1);
                                                        } else if ("MONTH".equals(groupBy)) {
                                                                int year = ((Number) r[0]).intValue();
                                                                int month = ((Number) r[1]).intValue();
                                                                return LocalDate.of(year, month, 1);
                                                        } else {
                                                                return ((java.sql.Date) r[0]).toLocalDate();
                                                        }
                                                },
                                                r -> (BigDecimal) r[r.length - 1] // 假設最後一個欄位是 SUM 的金額
                                ));
                // 6. 補齊區間內沒有訂單的日期/月份/年份，確保前端圖表連續
                List<RevenueTrendResponse> trendList = new ArrayList<>();

                if ("YEAR".equals(groupBy)) {
                        for (int y = startDate.getYear(); y <= endDate.getYear(); y++) {
                                LocalDate date = LocalDate.of(y, 1, 1);
                                trendList.add(new RevenueTrendResponse(date,
                                                revenueMap.getOrDefault(date, BigDecimal.ZERO)));
                        }
                } else if ("MONTH".equals(groupBy)) {
                        YearMonth current = YearMonth.from(startDate);
                        YearMonth endYm = YearMonth.from(endDate);
                        while (!current.isAfter(endYm)) {
                                LocalDate date = current.atDay(1);
                                trendList.add(new RevenueTrendResponse(date,
                                                revenueMap.getOrDefault(date, BigDecimal.ZERO)));
                                current = current.plusMonths(1);
                        }
                } else { // DAY
                        long days = ChronoUnit.DAYS.between(startDate, endDate);
                        for (int i = 0; i <= days; i++) {
                                LocalDate currentDate = startDate.plusDays(i);
                                trendList.add(new RevenueTrendResponse(currentDate,
                                                revenueMap.getOrDefault(currentDate, BigDecimal.ZERO)));
                        }
                }

                return new RevenueDetailResponse(groupBy.toLowerCase(), totalRevenue, previousRevenue, changeRate,
                                trendList);

        }

        // 計算環比百分比
        private Double calcChangeRate(BigDecimal current, BigDecimal previous) {
                if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
                        return null; // 上期沒營收，無法計算
                }
                return current.subtract(previous)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(previous, 1, RoundingMode.HALF_UP) // 保留一位小數
                                .doubleValue();
        }

         
        

private static final String[] BUCKET_LABELS = { "$0-99", "$100-199", "$200-299", "$300以上" };

private static int bucketIndex(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.valueOf(100)) < 0) return 0;
    if (amount.compareTo(BigDecimal.valueOf(200)) < 0) return 1;
    if (amount.compareTo(BigDecimal.valueOf(300)) < 0) return 2;
    return 3;
}

@Transactional(readOnly = true)
public OrderValueAnalysisResponse getOrderValueAnalysis(OrderValueAnalysisRequest request) {

    // 1. 預設為「今日」（明確指定時區，避免伺服器時區不同造成日期錯位）
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Taipei"));
    LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : today;
    LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : today;

    if (endDate.isBefore(startDate)) {
        throw new IllegalArgumentException("結束日期不能早於開始日期");
    }

    // 半開區間 [start, end)：end 是「結束日的隔天 00:00」，不要用 LocalTime.MAX
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.plusDays(1).atStartOfDay();

    OrderValueAnalysisResponse response = new OrderValueAnalysisResponse();

    // ==========================================
    // A. 客單價分佈（長條圖）
    // ==========================================
    long[] counts = new long[BUCKET_LABELS.length];
    for (BigDecimal amount : salesRepo.findOrderAmountsBetween(start, end)) {
        if (amount == null) continue; // 防 NPE
        counts[bucketIndex(amount)]++;
    }

    // 固定四個區間、固定順序，沒有訂單的區間也回 0，前端圖表才不會缺柱子
    List<Map<String, Object>> distributionList = new ArrayList<>();
    for (int i = 0; i < BUCKET_LABELS.length; i++) {
        distributionList.add(Map.<String, Object>of(
                "range", BUCKET_LABELS[i],
                "count", counts[i]));
    }
    response.setOrderValueDistribution(distributionList);

    // ==========================================
    // B. 支付方式 × 客單價（圓餅圖 + 列表）
    // ==========================================
    List<Map<String, Object>> paymentStatsList = new ArrayList<>();
    for (Object[] row : salesRepo.findPaymentMethodStats(start, end)) {
        String method = row[0] != null ? row[0].toString() : "未知";
        BigDecimal avgAmount = row[1] != null
                ? new BigDecimal(row[1].toString()).setScale(0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        long orderCount = row[2] != null ? ((Number) row[2]).longValue() : 0L;

        Map<String, Object> stat = new HashMap<>();
        stat.put("method", method);
        stat.put("avgAmount", avgAmount);
        stat.put("orderCount", orderCount);
        paymentStatsList.add(stat);
    }

    // 平均客單價由高到低
    paymentStatsList.sort(
            Comparator.comparing((Map<String, Object> m) -> (BigDecimal) m.get("avgAmount")).reversed());

    response.setPaymentMethodStats(paymentStatsList);

    return response;
}

@Transactional(readOnly = true)
public CostDetailResponse getCostDetail(LocalDate startDate, LocalDate endDate, String groupBy) {
 
    if (endDate.isBefore(startDate)) {
        throw new IllegalArgumentException("結束日期不能早於開始日期");
    }
    if (!List.of("DAY", "MONTH", "YEAR").contains(groupBy)) {
        throw new IllegalArgumentException("groupBy 只能是 DAY、MONTH 或 YEAR");
    }
 
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.plusDays(1).atStartOfDay();
 
    // 先把區間內每個時間桶填 0，沒有訂單的日期/月份圖表也不會斷掉
    Map<String, BigDecimal> buckets = new LinkedHashMap<>();
 
    switch (groupBy) {
        case "MONTH" -> {
            YearMonth last = YearMonth.from(endDate);
            for (YearMonth ym = YearMonth.from(startDate); !ym.isAfter(last); ym = ym.plusMonths(1)) {
                buckets.put(ym.toString(), BigDecimal.ZERO); // yyyy-MM
            }
            for (Object[] r : itemRepo.getCostGroupedByMonth(start, end)) {
                String key = String.format("%04d-%02d",
                        ((Number) r[0]).intValue(), ((Number) r[1]).intValue());
                buckets.put(key, toBigDecimal(r[2]));
            }
        }
        case "YEAR" -> {
            for (int y = startDate.getYear(); y <= endDate.getYear(); y++) {
                buckets.put(String.valueOf(y), BigDecimal.ZERO);
            }
            for (Object[] r : itemRepo.getCostGroupedByYear(start, end)) {
                buckets.put(String.valueOf(((Number) r[0]).intValue()), toBigDecimal(r[1]));
            }
        }
        default -> { // DAY
            for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
                buckets.put(d.toString(), BigDecimal.ZERO); // yyyy-MM-dd
            }
            for (Object[] r : itemRepo.getCostGroupedByDay(start, end)) {
                buckets.put(r[0].toString(), toBigDecimal(r[1]));
            }
        }
    }
 
        List<CostDetailResponse.TrendPoint> trend = buckets.entrySet().stream()
                .map(e -> new CostDetailResponse.TrendPoint(e.getKey(), e.getValue()))
                .toList();
        
        // 本期 / 上期（上期 = 緊接在前、天數相同的區間；請和營收頁的定義對齊）
        BigDecimal total = nullToZero(itemRepo.sumCostBetween(start, end));
        
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate prevEndDate = startDate.minusDays(1);
        LocalDate prevStartDate = prevEndDate.minusDays(days - 1);
        BigDecimal previous = nullToZero(itemRepo.sumCostBetween(
                prevStartDate.atStartOfDay(), prevEndDate.plusDays(1).atStartOfDay()));
        
        BigDecimal changeRate = previous.signum() == 0
                ? null
                : total.subtract(previous)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(previous, 1, RoundingMode.HALF_UP);

        
        
        CostDetailResponse response = new CostDetailResponse();
   
        response.setTotalCost(total);
        response.setPreviousCost(previous);
        response.setChangeRate(changeRate);
        response.setMissingCostCount(itemRepo.countItemsMissingCost(start, end));
        response.setDailyTrend(trend);
        return response;
        }
 
private static BigDecimal toBigDecimal(Object value) {
    return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
}
 
private static BigDecimal nullToZero(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
}



}
