package com.example.demo.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dashboard.dto.DashboardResponse;
import com.example.demo.dashboard.dto.RevenueTrendResponse;
import com.example.demo.dashboard.dto.TopProductResponse;
import com.example.demo.salesOrder.SalesOrderItemRepository;
import com.example.demo.salesOrder.SalesOrderRepository;

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


        LocalDateTime weekStart = today.minusDays(6).atStartOfDay();

        List<RevenueTrendResponse> trend =
                salesRepo.getWeeklyRevenue(weekStart).stream()
                        .map(r -> new RevenueTrendResponse(
                                ((java.sql.Date) r[0]).toLocalDate(),
                                (BigDecimal) r[1]))
                        .toList();

        List<TopProductResponse> top =
                itemRepo.findTopProducts(PageRequest.of(0, 5)).stream()
                        .map(r -> new TopProductResponse(
                                (String) r[0],
                                (BigDecimal) r[1]))
                        .toList();

        // 放進 DashboardResponse
        response.setWeeklyRevenue(trend);
        response.setTopProducts(top);

        return response;
    }
}
