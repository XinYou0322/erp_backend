package com.example.demo.dashboard.dto;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CostDetailResponse {
    private BigDecimal totalCost;
    private BigDecimal previousCost;
    private LocalDate previousStartDate; // 新增：上期起日
    private LocalDate previousEndDate; // 新增：上期迄日
    private BigDecimal changeRate; // null = 上期無資料
    private long missingCostCount; // 缺成本的明細筆數
    private List<TrendPoint> dailyTrend; // 欄位名稱沿用營收頁

    public record TrendPoint(String date, BigDecimal cost) {
    }
}