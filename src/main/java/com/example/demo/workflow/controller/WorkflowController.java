package com.example.demo.workflow.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.workflow.dto.ApproveWorkflowRequest;
import com.example.demo.workflow.dto.CreateWorkflowRequest;
import com.example.demo.workflow.dto.WorkflowLogResponse;
import com.example.demo.workflow.dto.WorkflowResponse;
import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.entity.WorkflowLog;
import com.example.demo.workflow.repository.WorkflowLogRepository;
import com.example.demo.workflow.service.WorkflowService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowService workflowService;
    private final WorkflowLogRepository worklogRespo;

    @PostMapping
    public WorkflowResponse create(@Valid @RequestBody CreateWorkflowRequest request) {

        Workflow workflow = workflowService.startWorkflow(request);

        return WorkflowResponse.from(workflow);
    }

    @GetMapping("/{id}")
    public WorkflowResponse getWorkflow(@PathVariable Long id) {

        Workflow workflow = workflowService.getWorkflowOrThrow(id);

        return WorkflowResponse.from(workflow);
    }

    @GetMapping("/{id}/logs")
    public List<WorkflowLogResponse> getLogs(@PathVariable Long id) {
        return workflowService.getLogs(id)
                .stream()
                .map(WorkflowLogResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/pending")
    public List<WorkflowResponse> getPending(@RequestParam Long approverId) {
        return workflowService.getPendingByApprover(approverId);
    }

    @GetMapping
    public List<WorkflowResponse> getWorkflows(@RequestParam Long approverId) {
        return workflowService.getAllByApprover(approverId);
    }

    // 核准
    @PatchMapping("{id}/approve")
    public ResponseEntity<WorkflowResponse> approveWorkflow(@PathVariable Long id,
            @Valid @RequestBody ApproveWorkflowRequest request) {

        Workflow workflow = workflowService.approve(id, request);
        return ResponseEntity.ok(WorkflowResponse.from(workflow));
    }

    // 駁回
    @PatchMapping("{id}/reject")
    public ResponseEntity<WorkflowResponse> rejectWorkflow(@PathVariable Long id,
            @Valid @RequestBody ApproveWorkflowRequest request) {

        Workflow workflow = workflowService.reject(id, request);
        return ResponseEntity.ok(WorkflowResponse.from(workflow));
    }

}
