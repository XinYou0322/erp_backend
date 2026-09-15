package com.example.demo.leave;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
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
@Slf4j // 加入 Lombok log
public class LeaveRequestEventListener {

private final LeaveRequestRepository leaveRepo;

@EventListener 
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void handleWorkflowCompleted(WorkflowStatusChangedEvent event) {
    log.info("收到簽核事件: docId={}, type={}, status={}",
    event.documentId(), event.documentType(), event.newStatus());

    if (event.documentType() != DocumentType.LEAVE) {
    return;
    }

    // 3. 改用 orElse(null) + log，避免異常被靜默吞掉
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
    log.info("請假單狀態已同步更新: leaveId={}, status={}", leave.getId(),
    leave.getStatus());
    }
}