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
import com.example.demo.leave.enums.LeaveDurationType;
import com.example.demo.leave.enums.LeaveStatus;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;
import com.example.demo.workflow.dto.CreateWorkflowRequest;
import com.example.demo.workflow.entity.Workflow;
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
    @Transactional
    public LeaveRequest createLeaveRequest(CreateLeaveRequest request) {

        User applicant = userRepo.findById(request.getApplicantId())
            .orElseThrow(() -> new RuntimeException("找不到申請人"));

        LeaveRequest leave = new LeaveRequest();
        leave.setApplicant(applicant);
        leave.setLeaveType(request.getLeaveType());
        leave.setLeaveDurationType(request.getLeaveDurationType()); // null 會在下面補成 FULL_DAY
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setStartTime(request.getStartTime());
        leave.setEndTime(request.getEndTime());
        leave.setReason(request.getReason());
        leave.setStatus(LeaveStatus.DRAFT);

        normalizeAndValidate(leave);

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
        if (request.getLeaveDurationType() != null) {
            leave.setLeaveDurationType(request.getLeaveDurationType());
        }
        if (request.getStartDate() != null) {
            leave.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            leave.setEndDate(request.getEndDate());
        }
        if (request.getStartTime() != null) {
            leave.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            leave.setEndTime(request.getEndTime());
        }
        if (request.getReason() != null) {
            leave.setReason(request.getReason());
        }

        // 用「合併後的最終狀態」驗證，而不是只驗證傳進來的欄位
        normalizeAndValidate(leave);

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

    // 刪除請假單 (僅限草稿)
    @Transactional
    public void deleteLeaveRequest(Long id) {
        LeaveRequest leave = getLeaveRequestOrThrow(id);

        // 1. 狀態檢查：只有草稿可以刪除
        if (leave.getStatus() != LeaveStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態的請假單可以刪除");
        }

        // 2. (可選) 權限檢查：確保是申請人本人刪除，防止 A 員工刪除 B 員工的草稿
        // Long currentUserId = getCurrentUserId(); // 從 SecurityContext 取得
        // if (!leave.getApplicant().getId().equals(currentUserId)) {
        // throw new AccessDeniedException("您無權刪除此請假單");
        // }

        // 3. 執行刪除
        leaveRepo.deleteById(id);
    }

    private void normalizeAndValidate(LeaveRequest leave) {
        if (leave.getLeaveDurationType() == null) {
            leave.setLeaveDurationType(LeaveDurationType.FULL_DAY); // 相容舊版前端
        }

        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException("請選擇請假日期");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("結束日期不能早於開始日期");
        }

        if (leave.getLeaveDurationType() == LeaveDurationType.PARTIAL_DAY) {
            if (!leave.getStartDate().equals(leave.getEndDate())) {
                throw new IllegalArgumentException("部分時段請假只能是同一天");
            }
            if (leave.getStartTime() == null || leave.getEndTime() == null) {
                throw new IllegalArgumentException("請填寫請假時間");
            }
            if (!leave.getEndTime().isAfter(leave.getStartTime())) {
                throw new IllegalArgumentException("結束時間必須晚於開始時間");
            }
        } else {
            // 全天：清掉時間，避免從部分時段切回全天時留下髒資料
            leave.setStartTime(null);
            leave.setEndTime(null);
        }
    }

}
