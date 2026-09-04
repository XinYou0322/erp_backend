package com.example.demo.workflow.dto;

import lombok.Data;

import com.example.demo.users.User;
import com.example.demo.workflow.enums.DocumentType;

import jakarta.validation.constraints.NotBlank;

@Data
public class CreateWorkflowRequest {
    private DocumentType documentType;

    private Long documentId;

    private Long applicantId;

    private Long approverId; // 目前先保留，以後可改成後端自動找主管

    @NotBlank(message = "申請原因必須填寫")
    private String remark;
}
