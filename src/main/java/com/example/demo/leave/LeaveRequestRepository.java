package com.example.demo.leave;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.users.User;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // Codex 修改：一次查詢與行事曆月份重疊的本人假單，包含跨月假單。
    List<LeaveRequest> findByApplicantIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long applicantId, java.time.LocalDate end, java.time.LocalDate start);

    List<LeaveRequest> findByApplicantOrderByCreatedAtDesc(User applicant);
}
