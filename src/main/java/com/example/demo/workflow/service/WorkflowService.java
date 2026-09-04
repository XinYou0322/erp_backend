package com.example.demo.workflow.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;
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

    private void saveLog(Workflow workflow, User operator, WorkflowAction action, String remark) {

        WorkflowLog log = new WorkflowLog();
        log.setWorkflow(workflow);
        log.setOperator(operator);
        log.setAction(action);
        log.setRemark(remark);

        worklogRespo.save(log);
    }

    public Workflow getWorkflowOrThrow(Long id) {
        return worksRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 workflow: " + id));
    }

    public List<WorkflowResponse> getPendingByApprover(Long approverId) {
        User approver = userRepo.findById(approverId)
                .orElseThrow(() -> new RuntimeException("找不到使用者: " + approverId));
        List<Workflow> workflows = worksRepo.findByApproverAndStatus(approver, WorkflowStatus.PENDING);

        return workflows.stream().map(workflow -> {

            WorkflowResponse res = WorkflowResponse.from(workflow);

            worklogRespo.findFirstByWorkflowAndActionOrderByCreatedAtAsc(workflow, WorkflowAction.SUBMIT)
                    .ifPresent(log -> res.setRemark(log.getRemark()));

            return res;

        }).toList();
    }

    // public Workflow approve(Long workflowId, ApproveWorkflowRequest request)

    // public Workflow reject(Long workflowId, ApproveWorkflowRequest request)

}
