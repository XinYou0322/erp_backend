package com.example.demo.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dashboard.dto.DashboardResponse;
import com.example.demo.dashboard.dto.HourlySalesResponse;
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
        response.setTodayRevenue(salesRepo.getRevenueBetween(start, end));

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
}
