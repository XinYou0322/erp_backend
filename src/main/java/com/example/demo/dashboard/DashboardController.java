package com.example.demo.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dashboard.dto.DashboardResponse;
import com.example.demo.dashboard.dto.RevenueDetailResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse getDashboard(){

        return dashboardService.getDashboard();
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueDetailResponse> getRevenueDetail(
            @RequestParam(defaultValue = "week") String range) {
        return ResponseEntity.ok(dashboardService.getRevenueDetail(range));
    }
}
