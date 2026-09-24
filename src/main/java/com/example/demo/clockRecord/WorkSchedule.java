package com.example.demo.clockRecord;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 排班設定。
 * userId 為 null 時代表「預設排班」，套用給所有沒有個別設定的員工；
 * 若某員工有專屬排班，會優先套用該筆設定。
 */
@Getter
@Setter
@Entity
@Table(name = "work_schedules")
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // 遲到緩衝分鐘數：上班時間 + 緩衝 後才打卡才算遲到
    @Column(name = "late_grace_minutes", nullable = false)
    private Integer lateGraceMinutes = 0;

    // 早退緩衝分鐘數：下班時間 - 緩衝 前打卡才算早退
    @Column(name = "early_leave_grace_minutes", nullable = false)
    private Integer earlyLeaveGraceMinutes = 0;

    // 上班日，以逗號分隔的 ISO DayOfWeek 數字 (1=一 ... 7=日)，預設週一到週五
    @Column(name = "workdays", nullable = false)
    private String workdays = "1,2,3,4,5";

    public WorkSchedule() {
    }

    public WorkSchedule(String userId, LocalTime startTime, LocalTime endTime) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
