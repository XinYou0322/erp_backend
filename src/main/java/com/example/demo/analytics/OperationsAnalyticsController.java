package com.example.demo.analytics;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.analytics.dto.MaterialUsageAnalysisResponse;
import com.example.demo.analytics.dto.ReplenishmentSuggestionResponse;
import com.example.demo.analytics.dto.DailyPreparationSuggestionResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class OperationsAnalyticsController {

    private final OperationsAnalyticsService operationsAnalyticsService;

    @GetMapping("/replenishment")
    public ResponseEntity<List<ReplenishmentSuggestionResponse>> getReplenishmentSuggestions(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "7") int historyDays,
            @RequestParam(defaultValue = "7") int forecastDays) {
        return ResponseEntity.ok(operationsAnalyticsService.getReplenishmentSuggestions(
                date, historyDays, forecastDays));
    }

    @GetMapping("/material-usage")
    public ResponseEntity<List<MaterialUsageAnalysisResponse>> getMaterialUsageAnalysis(
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(operationsAnalyticsService.getMaterialUsageAnalysis(date));
    }

    @GetMapping("/daily-preparation-suggestions")
    public ResponseEntity<List<DailyPreparationSuggestionResponse>> getDailyPreparationSuggestions(
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(
                operationsAnalyticsService.getDailyPreparationSuggestions(date));
    }
}
