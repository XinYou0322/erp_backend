package com.example.demo.workflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.workflow.dto.CreateWorkflowRequest;
import com.example.demo.workflow.dto.WorkflowResponse;
import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.service.WorkflowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowService workflowService;

    @PostMapping
    public WorkflowResponse create(@RequestBody CreateWorkflowRequest request) {

        Workflow workflow = workflowService.startWorkflow(request);

        return WorkflowResponse.from(workflow);
    }

    @GetMapping("/{id}")
    public WorkflowResponse getWorkflow(@PathVariable Long id){

        Workflow workflow = workflowService.getWorkflowOrThrow(id);

        return WorkflowResponse.from(workflow);
    }
   
}
