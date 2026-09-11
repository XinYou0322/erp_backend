package com.example.demo.workflow.dto;

import java.time.LocalDateTime;

import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.entity.WorkflowLog;
import com.example.demo.workflow.enums.WorkflowAction;

import lombok.Data;

@Data
public class WorkflowLogResponse {
    private Long id;
    private WorkflowAction action;
    private String operator;
    private String remark;
    private LocalDateTime createdAt;

    public static WorkflowLogResponse from(WorkflowLog log) {
        WorkflowLogResponse res = new WorkflowLogResponse();
        res.id = log.getId();

        res.action = log.getAction();
        res.operator = log.getOperator().getName();
        res.remark = log.getRemark();

        res.createdAt = LocalDateTime.ofInstant(log.getCreatedAt(), java.time.ZoneId.systemDefault());

        return res;
    }
}
