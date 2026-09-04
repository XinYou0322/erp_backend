package com.example.demo.workflow.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.workflow.dto.CreateWorkflowRequest;
import com.example.demo.workflow.dto.WorkflowResponse;
import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.entity.WorkflowLog;
import com.example.demo.workflow.repository.WorkflowLogRepository;
import com.example.demo.workflow.service.WorkflowService;

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

    @GetMapping("/pending")
    public List<WorkflowResponse> getPending(@RequestParam Long approverId) {
        return workflowService.getPendingByApprover(approverId);
    }

    @GetMapping("/{id}/logs")
    public List<WorkflowLog> getLogs(@PathVariable Long id) {
        return worklogRespo.findByWorkflowIdOrderByCreatedAtAsc(id);
    }

}
