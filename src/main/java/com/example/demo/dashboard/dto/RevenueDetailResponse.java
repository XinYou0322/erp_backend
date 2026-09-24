package com.example.demo.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueDetailResponse {
    private String range; // "week" 或 "month"
    private BigDecimal totalRevenue; // 總營收
    private BigDecimal previousRevenue;  // 新增：上期總營收
    private Double changeRate;           // 新增：環比增減 %（上期為 0 時為 null）
    private List<RevenueTrendResponse> dailyTrend; // 每日明細
}