package com.example.demo.workflow.dto;

import lombok.Data;

import com.example.demo.users.User;
import com.example.demo.workflow.enums.DocumentType;

@Data
public class CreateWorkflowRequest {
    private DocumentType documentType;

    private Long documentId;

    private Long applicantId;

    private Long approverId;
}
