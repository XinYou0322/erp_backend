package com.example.demo.clockRecord;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    // 查詢特定員工的專屬排班
    Optional<WorkSchedule> findByUserId(String userId);

    // 查詢預設排班 (userId 為 null)
    Optional<WorkSchedule> findByUserIdIsNull();
}
