package com.example.demo.workflow.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;
import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.entity.WorkflowLog;
import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.enums.WorkflowAction;
import com.example.demo.workflow.enums.WorkflowStatus;
import com.example.demo.workflow.repository.WorkflowLogRepository;
import com.example.demo.workflow.repository.WorkflowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository worksRepo;
    private final WorkflowLogRepository worklogRespo;
    private final UsersRepository userRepo;

    @Transactional
    // public Workflow startWorkflow(DocumentType documentType, Long documentId,
    // Long applicantId) {

    // }

    private void saveLog(Workflow workflow, User operator, WorkflowAction action, String remark) {

        WorkflowLog log = new WorkflowLog();
        log.setWorkflow(workflow);
        log.setOperator(operator);
        log.setAction(action);
        log.setRemark(remark);
        log.onCreate();

        worksRepo.save(null);
    }

}
