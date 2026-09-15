package com.example.demo.leave;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.demo.leave.dto.CreateLeaveRequest;
import com.example.demo.leave.dto.LeaveRequestResponse;
import com.example.demo.leave.dto.UpdateLeaveRequest;
import com.example.demo.leave.enums.LeaveStatus;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;
import com.example.demo.workflow.dto.CreateWorkflowRequest;
import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.enums.WorkflowStatus;
import com.example.demo.workflow.event.WorkflowStatusChangedEvent;
import com.example.demo.workflow.service.WorkflowService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRepo;
    private final UsersRepository userRepo;
    private final WorkflowService workflowService;

    // 新增請假單
    public LeaveRequest createLeaveRequest(CreateLeaveRequest request) {

        // 驗證請假天數日期區間是否合法
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("結束日期不能早於開始日期");
        }
        User applicant = userRepo.findById(request.getApplicantId())
                .orElseThrow(() -> new RuntimeException("找不到申請人"));

        // 建立LeaveRequest entity並存檔
        LeaveRequest leave = new LeaveRequest();
        leave.setApplicant(applicant);
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());
        leave.setStatus(LeaveStatus.DRAFT);

        return leaveRepo.save(leave);

    }

    // 送出請假單
    @Transactional
    public LeaveRequest submitLeaveRequest(Long leaveId, Long approverId) {

        LeaveRequest leave = getLeaveRequestOrThrow(leaveId);

        if (leave.getStatus() != LeaveStatus.DRAFT) {
            throw new IllegalStateException("只有草稿才能送出");
        }

        CreateWorkflowRequest workflowRequest = new CreateWorkflowRequest();

        workflowRequest.setDocumentType(DocumentType.LEAVE);
        workflowRequest.setDocumentId(leave.getId());
        workflowRequest.setApplicantId(leave.getApplicant().getId());
        workflowRequest.setApproverId(approverId);
        workflowRequest.setRemark(leave.getReason());

        workflowService.startWorkflow(workflowRequest);

        leave.setStatus(LeaveStatus.PENDING);

        return leaveRepo.save(leave);
    }

    // 列出某員工所有請假單
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getByApplicant(Long applicantId) {

        User applicant = userRepo.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        return leaveRepo.findByApplicantOrderByCreatedAtDesc(applicant)
                .stream()
                .map(LeaveRequestResponse::from)
                .toList();
    }

    // 用id取得請假單
    @Transactional(readOnly = true)
    public LeaveRequest getLeaveRequestOrThrow(Long id) {
        return leaveRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到請假單：" + id));
    }

    // 更新請假單
    @Transactional
    public LeaveRequest updateLeaveRequest(Long id, UpdateLeaveRequest request) {

        LeaveRequest leave = getLeaveRequestOrThrow(id);

        if (leave.getStatus() != LeaveStatus.DRAFT) {
            throw new IllegalStateException("只有草稿可以修改");
        }

        if (request.getLeaveType() != null) {
            leave.setLeaveType(request.getLeaveType());
        }
        if (request.getStartDate() != null) {
            leave.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            leave.setEndDate(request.getEndDate());
        }
        if (request.getReason() != null) {
            leave.setReason(request.getReason());
        }

        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            if (leave.getEndDate().isBefore(leave.getStartDate())) {
                throw new IllegalArgumentException("結束日期不能早於開始日期");
            }
        }

        return leaveRepo.save(leave);
    }

    // 取消請假單
    @Transactional
    public LeaveRequest cancelLeaveRequest(Long id) {

        LeaveRequest leave = getLeaveRequestOrThrow(id);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("只有審核中的請假單可以取消");
        }

        leave.setStatus(LeaveStatus.CANCELLED);

        LeaveRequest updated = leaveRepo.save(leave);

        // 再取消 Workflow
        workflowService.cancelWorkflow(id, DocumentType.LEAVE);

        return updated;
    }

    // 更新狀態
    @Transactional
    public void updateStatus(Long leaveId, LeaveStatus status) {

        LeaveRequest leave = getLeaveRequestOrThrow(leaveId);

        // 簡單的狀態機防護：只允許從 PENDING 轉換到終態
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("無法從當前狀態轉換為: " + status);
        }

        leave.setStatus(status);

        leaveRepo.save(leave);
    }

}
