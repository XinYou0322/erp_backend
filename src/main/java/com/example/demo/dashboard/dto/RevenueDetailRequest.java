package com.example.demo.dashboard.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RevenueDetailRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String groupBy; // 期望值: "DAY", "MONTH", "YEAR"

    // Getters and Setters (或使用 @Data / @Getter @Setter)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getGroupBy() {
        return groupBy != null ? groupBy.toUpperCase() : "DAY";
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
}
