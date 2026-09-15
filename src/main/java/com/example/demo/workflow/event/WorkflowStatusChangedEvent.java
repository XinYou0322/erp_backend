package com.example.demo.workflow.event;

import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.enums.WorkflowStatus;

public record WorkflowStatusChangedEvent(Long documentId, DocumentType documentType, WorkflowStatus newStatus) {
}
