package com.example.demo.workflow.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class ApproveWorkflowRequest {

    private Long approverId;

    private String remark;
}
