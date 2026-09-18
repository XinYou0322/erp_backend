package com.example.demo.clockRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClockRecordRepository extends JpaRepository<ClockRecord, Long> {

    // 根據員工編號查詢所有的打卡紀錄
    List<ClockRecord> findByUserIdOrderByClockTimeDesc(String userId);

    // 查詢全部打卡紀錄，依時間倒序
    List<ClockRecord> findAllByOrderByClockTimeDesc();
}
