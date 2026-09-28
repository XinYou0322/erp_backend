package com.example.demo.dashboard;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dashboard.dto.CostDetailResponse;
import com.example.demo.dashboard.dto.DashboardResponse;
import com.example.demo.dashboard.dto.RevenueDetailRequest;
import com.example.demo.dashboard.dto.RevenueDetailResponse;
import com.example.demo.dashboard.dto.MaterialConsumptionResponse;
import com.example.demo.dashboard.dto.OrderValueAnalysisRequest;
import com.example.demo.dashboard.dto.OrderValueAnalysisResponse;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/material-consumption")
    public ResponseEntity<List<MaterialConsumptionResponse>> getMaterialConsumption(
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(dashboardService.getMaterialConsumption(date));
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueDetailResponse> getRevenueDetail(RevenueDetailRequest request) {

        // 防禦性編程：確保日期邏輯正確 (如果前端傳入的開始日晚於結束日，自動交換或拋出錯誤)
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                // 選項 A: 自動交換 (較友善)
                LocalDate temp = request.getStartDate();
                request.setStartDate(request.getEndDate());
                request.setEndDate(temp);

                // 選項 B: 拋出異常 (較嚴格)
                // throw new IllegalArgumentException("開始日期不能晚於結束日期");
            }
        }

        // 呼叫重構後的 Service
        RevenueDetailResponse response = dashboardService.getRevenueDetail(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/order-value-analysis")
    public ResponseEntity<OrderValueAnalysisResponse> getOrderValueAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        OrderValueAnalysisRequest request = new OrderValueAnalysisRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        
        return ResponseEntity.ok(dashboardService.getOrderValueAnalysis(request));
    }

    @GetMapping("/cost-detail")
    public ResponseEntity<CostDetailResponse> getCostDetail(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAY") String groupBy) {
    
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Taipei"));
        return ResponseEntity.ok(dashboardService.getCostDetail(
                startDate != null ? startDate : today,
                endDate != null ? endDate : today,
                groupBy));
    }
}
