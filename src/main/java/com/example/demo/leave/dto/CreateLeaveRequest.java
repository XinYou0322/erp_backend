package com.example.demo.leave.dto;

import java.time.LocalDate;

import com.example.demo.leave.LeaveType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLeaveRequest {
    private LeaveType leaveType;

    private Long documentId;

    private Long applicantId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    // private Long approverId;

    @NotBlank(message = "申請原因必須填寫")
    private String reason;
}
