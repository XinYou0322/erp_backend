package com.example.demo.leave;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.demo.leave.enums.LeaveStatus;
import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.event.WorkflowStatusChangedEvent;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestEventListener {

    private final LeaveRequestRepository leaveRepo;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleWorkflowCompleted(WorkflowStatusChangedEvent event) {
        if (event.documentType() != DocumentType.LEAVE) {
            return;
        }
        try {
            LeaveRequest leave = leaveRepo.findById(event.documentId()).orElse(null);
            if (leave == null) {
                log.error("找不到請假單, documentId={}", event.documentId());
                return;
            }
            switch (event.newStatus()) {
                case APPROVED -> leave.setStatus(LeaveStatus.APPROVED);
                case REJECTED -> leave.setStatus(LeaveStatus.REJECTED);
                case CANCELLED -> leave.setStatus(LeaveStatus.CANCELLED);
                default -> {
                    log.warn("未處理的簽核狀態: {}", event.newStatus());
                    return;
                }
            }
            leaveRepo.save(leave);
        } catch (Exception e) {
            // 這裡吞掉，不讓例外往上傳播影響已經 commit 的 approve()
            log.error("同步請假單狀態失敗, documentId={}", event.documentId(), e);
            // 進階：可以寫進一張 failed_sync_log 表方便之後補償重試
        }
    }
}