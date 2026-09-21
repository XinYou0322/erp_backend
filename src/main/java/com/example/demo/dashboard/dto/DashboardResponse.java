package com.example.demo.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DashboardResponse {
    private BigDecimal todayRevenue;
    private Long todayOrders;
    private BigDecimal averageOrderAmount;

    private List<RevenueTrendResponse> weeklyRevenue;
    private List<TopProductResponse> topProducts;//長銷商品
    private List<TopProductResponse> recentTopProducts; // 最近7天熱門
    private List<TopRevenueResponse> topRevenueProducts;
    private List<TopRevenueResponse> recentTopRevenueProducts;
    private List<HourlySalesResponse> hourlySales;
}
