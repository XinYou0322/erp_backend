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
    private List<TopProductResponse> topProducts;
}
