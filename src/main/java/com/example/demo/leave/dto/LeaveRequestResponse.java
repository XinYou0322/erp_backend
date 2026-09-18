package com.example.demo.leave.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.leave.LeaveRequest;
import com.example.demo.leave.enums.LeaveStatus;
import com.example.demo.leave.enums.LeaveType;

import lombok.Data;

@Data
public class LeaveRequestResponse {

    private Long id;
    private Long applicantId;
    private String applicantName;

    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;

    private String reason;
    private LeaveStatus status;

    private LocalDateTime createdAt;

    public static LeaveRequestResponse from(LeaveRequest leave) {
        LeaveRequestResponse res = new LeaveRequestResponse();

        res.id = leave.getId();

        res.applicantId = leave.getApplicant().getId();
        res.applicantName = leave.getApplicant().getName();

        res.leaveType = leave.getLeaveType();
        res.startDate = leave.getStartDate();
        res.endDate = leave.getEndDate();

        res.reason = leave.getReason();
        res.status = leave.getStatus();

        res.createdAt = LocalDateTime.ofInstant(leave.getCreatedAt(), java.time.ZoneId.systemDefault());

        return res;
    }
}
