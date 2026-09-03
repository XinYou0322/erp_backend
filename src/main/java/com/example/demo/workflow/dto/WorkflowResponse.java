package com.example.demo.workflow.dto;

import java.time.LocalDateTime;

import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.enums.WorkflowStatus;

import lombok.Data;

@Data
public class WorkflowResponse {
    private Long id;
    private DocumentType documentType;
    private Long documentId;
    private WorkflowStatus status;
    private Long applicantId;
    private String applicantName;
    private Long approverId;
    private String approverName;

    public static WorkflowResponse from(Workflow w) {
        WorkflowResponse res = new WorkflowResponse();
        res.id = w.getId();
        res.documentType = w.getDocumentType();
        res.documentId = w.getDocumentId();
        res.status = w.getStatus();
        res.applicantId = w.getApplicant().getId();
        res.applicantName = w.getApplicant().getName();

        res.approverId = w.getApprover().getId();
        res.approverName = w.getApprover().getName();
        return res;
    }
}
