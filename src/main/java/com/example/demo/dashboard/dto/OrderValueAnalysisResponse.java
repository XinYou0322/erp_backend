package com.example.demo.dashboard.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OrderValueAnalysisResponse {
    private List<Map<String, Object>> orderValueDistribution; // 客單價分佈 (長條圖)
    private List<Map<String, Object>> paymentMethodStats;     // 支付方式 × 客單價 (圓餅圖+列表)

    // Getters and Setters
    // public void setOrderValueDistribution(List<Map<String, Object>> orderValueDistribution) {
    //     this.orderValueDistribution = orderValueDistribution;
    // }
    // public void setPaymentMethodStats(List<Map<String, Object>> paymentMethodStats) {
    //     this.paymentMethodStats = paymentMethodStats;
    // }
}
