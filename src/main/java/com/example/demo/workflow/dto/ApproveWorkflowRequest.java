package com.example.demo.workflow.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public class ApproveWorkflowRequest {

    private Long approverId;

    private String remark;
}
