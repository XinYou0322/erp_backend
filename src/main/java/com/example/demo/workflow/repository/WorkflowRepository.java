package com.example.demo.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.workflow.entity.Workflow;
import com.example.demo.workflow.enums.WorkflowStatus;

import java.util.List;
import java.util.Optional;

import com.example.demo.user.User;
import com.example.demo.workflow.enums.DocumentType;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    // 給審核的人看需要簽核的單據
    List<Workflow> findByApproverAndStatus(User approver, WorkflowStatus status);

    // 給申請人看申請過的單據紀錄
    List<Workflow> findByApplicant(User applicant);

    // 查詢特定表單的狀態
    Optional<Workflow> findByDocumentTypeAndDocumentId(DocumentType documentType, Long documentId);

}
