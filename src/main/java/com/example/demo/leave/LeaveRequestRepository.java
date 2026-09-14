package com.example.demo.leave;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.users.User;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByApplicantOrderByCreatedAtDesc(User applicant);
}
