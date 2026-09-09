package com.example.demo.workflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;
import com.example.demo.workflow.dto.ApproveWorkflowRequest;
import com.example.demo.workflow.dto.CreateWorkflowRequest;
import com.example.demo.workflow.dto.WorkflowResponse;
import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.entity.WorkflowLog;
import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.enums.WorkflowAction;
import com.example.demo.workflow.enums.WorkflowStatus;
import com.example.demo.workflow.repository.WorkflowLogRepository;
import com.example.demo.workflow.repository.WorkflowRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository worksRepo;
    private final WorkflowLogRepository worklogRespo;
    private final UsersRepository userRepo;

    // 新增一個簽核流程
    @Transactional
    public Workflow startWorkflow(CreateWorkflowRequest request) {
        Workflow workflow = new Workflow();
        User applicant = userRepo.findById(request.getApplicantId())
                .orElseThrow(() -> new RuntimeException("找不到申請人"));
        User approver = userRepo.findById(request.getApproverId())
                .orElseThrow(() -> new RuntimeException("找不到簽核人"));

        workflow.setDocumentType(request.getDocumentType());
        workflow.setDocumentId(request.getDocumentId());
        workflow.setApplicant(applicant);
        workflow.setApprover(approver);
        workflow.setStatus(WorkflowStatus.PENDING);
        Workflow savedWorkflow = worksRepo.save(workflow);
        saveLog(savedWorkflow, applicant, WorkflowAction.SUBMIT, request.getRemark());

        return savedWorkflow;

    }

    // 建立簽核紀錄
    private void saveLog(Workflow workflow, User operator, WorkflowAction action, String remark) {

        WorkflowLog log = new WorkflowLog();
        log.setWorkflow(workflow);
        log.setOperator(operator);
        log.setAction(action);
        log.setRemark(remark);

        worklogRespo.save(log);
    }

    // 查詢特定workflow
    public Workflow getWorkflowOrThrow(Long id) {
        return worksRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 workflow: " + id));
    }

    // 查詢特定審核人員
    private User getApproverOrThrow(Long approverId) {
        return userRepo.findById(approverId)
                .orElseThrow(() -> new RuntimeException("找不到使用者: " + approverId));
    }

    // 查詢審核人員的待辦事項
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getPendingByApprover(Long approverId) {
        User approver = getApproverOrThrow(approverId);
        List<Workflow> workflows = worksRepo.findByApproverAndStatus(approver, WorkflowStatus.PENDING);

        return workflows.stream().map(workflow -> {

            WorkflowResponse res = WorkflowResponse.from(workflow);

            worklogRespo.findFirstByWorkflowAndActionOrderByCreatedAtAsc(workflow, WorkflowAction.SUBMIT)
                    .ifPresent(log -> res.setRemark(log.getRemark()));

            return res;

        }).toList();
    }

    // 查詢審核人員的所有簽核單
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getAllByApprover(Long approverId) {
        User approver = getApproverOrThrow(approverId);

        return worksRepo.findByApproverOrderByCreatedAtDesc(approver)
                .stream()
                .map(workflow -> {
                    WorkflowResponse res = WorkflowResponse.from(workflow);

                    worklogRespo.findFirstByWorkflowAndActionOrderByCreatedAtAsc(
                            workflow, WorkflowAction.SUBMIT)
                            .ifPresent(log -> res.setRemark(log.getRemark()));

                    return res;
                })
                .toList();
    }

    // 查詢簽核紀錄
    public List<WorkflowLog> getLogs(long workflowId) {
        return worklogRespo.findByWorkflowIdOrderByCreatedAtAsc(workflowId);
    }

    @Transactional
    public Workflow approve(Long workflowId, ApproveWorkflowRequest request) {

        return updateStatus(workflowId, request, WorkflowStatus.APPROVED, WorkflowAction.APPROVE);
    }

    @Transactional
    public Workflow reject(Long workflowId, ApproveWorkflowRequest request) {

        if (request.getRemark() == null || request.getRemark().isBlank()) {
            throw new IllegalArgumentException("必須填寫原因");
        }
        return updateStatus(workflowId, request, WorkflowStatus.REJECTED, WorkflowAction.REJECT);
    }

    private Workflow updateStatus(long workflowId, ApproveWorkflowRequest request, WorkflowStatus status,
            WorkflowAction action) {

        Workflow workflow = getWorkflowOrThrow(workflowId);
        User approver = getApproverOrThrow(request.getApproverId());

        validateApprover(workflow, approver);
        validatePendingStatus(workflow);

        workflow.setStatus(status);
        Workflow update = worksRepo.save(workflow);

        saveLog(update, approver, action, request.getRemark());

        return update;
    }

    private void validateApprover(Workflow workflow, User approver) {
        if (workflow.getApprover() == null ||
                approver == null ||
                !Objects.equals(workflow.getApprover().getId(), approver.getId())) {

            throw new IllegalStateException("你不是此單的簽核人");
        }
    }

    private void validatePendingStatus(Workflow workflow) {
        if (workflow.getStatus() != WorkflowStatus.PENDING) {
            throw new IllegalStateException("此簽核單目前狀態為" + workflow.getStatus() + ",無法操重複操作");
        }
    }

}
