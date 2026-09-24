package com.example.demo.clockRecord;

import java.util.List;
import java.util.Map;

public class DailyAttendanceDto {

    private String date; // yyyy-MM-dd
    private AttendanceStatus status;
    private String clockInTime;  // 當天最早一筆上班打卡時間，無則 null
    private String clockOutTime; // 當天最晚一筆下班打卡時間，無則 null
    private List<Map<String, Object>> records; // 當天所有打卡明細

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    // 前端直接拿中文標籤顯示，不用自己再轉譯 enum
    public String getStatusLabel() {
        return status != null ? status.getLabel() : null;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(String clockInTime) {
        this.clockInTime = clockInTime;
    }

    public String getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(String clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public void setRecords(List<Map<String, Object>> records) {
        this.records = records;
    }
}
