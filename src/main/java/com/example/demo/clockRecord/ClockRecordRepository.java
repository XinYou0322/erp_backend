package com.example.demo.clockRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClockRecordRepository extends JpaRepository<ClockRecord, Long> {
    // Codex 修改：以使用者及當天半開時間區間查詢，排除隔天零點。
    List<ClockRecord> findByUserIdAndClockTimeGreaterThanEqualAndClockTimeLessThan(
            String userId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    // 根據員工編號查詢所有的打卡紀錄
    List<ClockRecord> findByUserIdOrderByClockTimeDesc(String userId);

    // 查詢全部打卡紀錄，依時間倒序
    List<ClockRecord> findAllByOrderByClockTimeDesc();
}
