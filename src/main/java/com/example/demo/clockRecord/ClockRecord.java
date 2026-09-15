package com.example.demo.clockRecord;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clock_records")
public class ClockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "clock_time", nullable = false)
    private LocalDateTime clockTime;

    @Column(name = "clock_type", nullable = false)
    private String clockType;

    // 建構子 (Constructor)
    public ClockRecord() {
    }

    public ClockRecord(String userId, LocalDateTime clockTime, String clockType) {
        this.userId = userId;
        this.clockTime = clockTime;
        this.clockType = clockType;
    }

}
