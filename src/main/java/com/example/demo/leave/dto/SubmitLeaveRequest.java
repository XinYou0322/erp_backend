package com.example.demo.leave.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitLeaveRequest {

    @NotNull
    private Long approverId;
}
