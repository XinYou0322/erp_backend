package com.example.demo.leave;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.demo.leave.enums.LeaveStatus;
import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.event.WorkflowStatusChangedEvent;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LeaveRequestEventListener {
    private final LeaveRequestRepository leaveRepo;

    // 監聽事件狀態
    @TransactionalEventListener
    public void handleWorkflowCompleted(WorkflowStatusChangedEvent event) {
        if (event.documentType() == DocumentType.LEAVE) {
            LeaveRequest leave = getLeaveRequestOrThrow(event.documentId());

            // 將 Workflow 的狀態同步映射到 LeaveRequest 的狀態
            // (假設您的 LeaveStatus 和 WorkflowStatus 有對應關係，例如 PENDING -> APPROVED)
            switch (event.newStatus()) {
                case APPROVED -> leave.setStatus(LeaveStatus.APPROVED);
                case REJECTED -> leave.setStatus(LeaveStatus.REJECTED);
                case CANCELLED -> leave.setStatus(LeaveStatus.CANCELLED);
                default -> {
                    return;
                }
            }
            leaveRepo.save(leave);
        }
    }

    private LeaveRequest getLeaveRequestOrThrow(Long documentId) {
        return leaveRepo.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("找不到請假單：" + documentId));
    }
}
