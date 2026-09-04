package com.example.demo.workflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.entity.WorkflowLog;
import com.example.demo.workflow.enums.WorkflowAction;

public interface WorkflowLogRepository extends JpaRepository<WorkflowLog, Long> {

    // 查詢某個workflow的完整歷程記錄
    List<WorkflowLog> findByWorkflowIdOrderByCreatedAtAsc(long workflowId);

    Optional<WorkflowLog> findFirstByWorkflowAndActionOrderByCreatedAtAsc(
            Workflow workflow,
            WorkflowAction action);
}
