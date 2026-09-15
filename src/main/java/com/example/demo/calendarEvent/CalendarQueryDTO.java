package com.example.demo.calendarEvent;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CalendarQueryDTO {

    private LocalDate startDate; // 依據目前月/週檢視的範圍
    private LocalDate endDate;
    private String searchQuery; // 模糊搜尋 (標題、負責人、地點、單號)
    private String statusFilter; // 狀態
    private String priorityFilter; // 優先級
    private String selectedCategory;// 分類
}
