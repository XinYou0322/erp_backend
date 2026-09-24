package com.example.demo.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dashboard.dto.DashboardResponse;
import com.example.demo.dashboard.dto.HourlySalesResponse;
import com.example.demo.dashboard.dto.RevenueDetailResponse;
import com.example.demo.dashboard.dto.RevenueTrendResponse;
import com.example.demo.dashboard.dto.TopProductResponse;
import com.example.demo.dashboard.dto.TopRevenueResponse;
import com.example.demo.salesOrder.SalesOrderItemRepository;
import com.example.demo.salesOrder.SalesOrderRepository;
import com.example.demo.salesOrder.SalesOrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SalesOrderRepository salesRepo;
    private final SalesOrderItemRepository itemRepo;

    public DashboardResponse getDashboard() {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        DashboardResponse response = new DashboardResponse();

        // 今日營收
        BigDecimal todayRevenue = salesRepo.getRevenueBetween(start, end);
        response.setTodayRevenue(todayRevenue);

        // 計算較昨日 
        LocalDateTime now = LocalDateTime.now();
        LocalDate yesterday = today.minusDays(1);
        BigDecimal yesterdaySamePeriod = salesRepo.getRevenueBetween(
        yesterday.atStartOfDay(),
        now.minusDays(1));

        response.setRevenueChangeRate(calcChangeRate(todayRevenue, yesterdaySamePeriod));

        // 今日訂單
        response.setTodayOrders(salesRepo.countOrdersBetween(start, end));

        // 平均客單價
        Double avg = salesRepo.getAverageOrderBetween(start, end);
        response.setAverageOrderAmount(BigDecimal.valueOf(avg));

        LocalDateTime weekStart = today.minusDays(7).atStartOfDay();
        LocalDateTime weekEnd = today.atStartOfDay(); // 今天0點，不含今天
        LocalDateTime startDate = LocalDate.now().minusDays(6).atStartOfDay();

        // 七天營收
        List<RevenueTrendResponse> rawTrend = salesRepo.getWeeklyRevenue(weekStart, weekEnd).stream()
        .map(r -> new RevenueTrendResponse(
                ((java.sql.Date) r[0]).toLocalDate(),
                (BigDecimal) r[1]))
        .toList();

        Map<LocalDate, BigDecimal> revenueByDate = rawTrend.stream()
                .collect(Collectors.toMap(RevenueTrendResponse::getDate, RevenueTrendResponse::getRevenue));

        List<RevenueTrendResponse> trend = IntStream.range(0, 7)
                .mapToObj(i -> {
                    LocalDate date = today.minusDays(7 - i); // i=0 -> 7天前, i=6 -> 昨天
                    BigDecimal revenue = revenueByDate.getOrDefault(date, BigDecimal.ZERO);
                    return new RevenueTrendResponse(date, revenue);
                })
                .toList();

        // 全部熱門商品 Top5
        List<TopProductResponse> top = itemRepo.findTopProducts(PageRequest.of(0, 5)).stream()
                .map(r -> new TopProductResponse(
                        (String) r[0],
                        (BigDecimal) r[1]))
                .toList();

        // 最近七天熱門商品 Top5
        List<TopProductResponse> recentTop = itemRepo
                .findTopProductsSince(startDate, PageRequest.of(0, 5))
                .stream()
                .map(r -> new TopProductResponse(
                        (String) r[0],
                        (BigDecimal) r[1]))
                .toList();

        // ===== 營收排行（新增）=====

        List<TopRevenueResponse> topRevenue = itemRepo
                .findTopProductsByRevenue(SalesOrderStatus.COMPLETED, PageRequest.of(0, 5)).stream()
                .map(r -> new TopRevenueResponse(
                        (String) r[0],
                        (BigDecimal) r[1]))
                .toList();

        List<TopRevenueResponse> recentTopRevenue = itemRepo.findTopProductsByRevenueSince(
                SalesOrderStatus.COMPLETED,
                weekStart,
                PageRequest.of(0, 5))
                .stream()
                .map(r -> new TopRevenueResponse(
                        (String) r[0],
                        (BigDecimal) r[1]))
                .toList();

        List<HourlySalesResponse> hourlySales = itemRepo.findHourlySales(SalesOrderStatus.COMPLETED.name(), today)
                .stream()
                .map(r -> new HourlySalesResponse(
                        ((Number) r[0]).intValue(),
                        (BigDecimal) r[1]))
                .toList();

        // 放進 DashboardResponse
        response.setWeeklyRevenue(trend);
        response.setTopProducts(top);
        response.setRecentTopProducts(recentTop);
        response.setTopRevenueProducts(topRevenue);
        response.setRecentTopRevenueProducts(recentTopRevenue);
        response.setHourlySales(hourlySales);

        return response;
    }

   

   public RevenueDetailResponse getRevenueDetail(String range) {
    LocalDate today = LocalDate.now();
    LocalDate startDate;
//     LocalDate prevStart;
//     LocalDate prevEnd;
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime prevStart;
    LocalDateTime prevEnd;

    // 1. 定義本期與上期的區間
    if ("month".equalsIgnoreCase(range)) {
    startDate = today.withDayOfMonth(1);
    // 上月同期：上月 1 號 → 上月的「現在這個時間點」
    prevStart = now.minusMonths(1).toLocalDate().withDayOfMonth(1).atStartOfDay();
    prevEnd = now.minusMonths(1);
    } else {
        startDate = today.minusDays(6);
        // 前 7 天同期：把本期窗口整體往前移 7 天
        prevStart = now.minusDays(13).toLocalDate().atStartOfDay();
        prevEnd = now.minusDays(7);
    }

    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = today.atTime(LocalTime.MAX);

    // 2. 本期與上期總營收（複用現有 query）
    BigDecimal totalRevenue = salesRepo.getRevenueBetween(start, end);
    BigDecimal previousRevenue = salesRepo.getRevenueBetween(prevStart, prevEnd);

    // 3. 計算環比百分比
    Double changeRate = calcChangeRate(totalRevenue, previousRevenue);

    // 4. 取得每日營收原始資料 
    List<Object[]> rawTrend = salesRepo.getWeeklyRevenue(start, end); 
    
    Map<LocalDate, BigDecimal> revenueMap = rawTrend.stream()
            .collect(Collectors.toMap(
                    r -> ((java.sql.Date) r[0]).toLocalDate(),
                    r -> (BigDecimal) r[1]
            ));

    // 5. 補齊沒有訂單的日期，並直接使用 RevenueTrendResponse
    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, today);
    List<RevenueTrendResponse> dailyTrend = new ArrayList<>();
    
    for (int i = 0; i <= daysBetween; i++) {
        LocalDate currentDate = startDate.plusDays(i);
        BigDecimal rev = revenueMap.getOrDefault(currentDate, BigDecimal.ZERO);
        
        dailyTrend.add(new RevenueTrendResponse(currentDate, rev));
    }

    return new RevenueDetailResponse(
            range, totalRevenue, previousRevenue, changeRate, dailyTrend);
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
}
