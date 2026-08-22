package com.example.demo.workflow.entity;

import java.time.LocalDateTime;
import com.example.demo.workflow.enums.ActionStatus;
//匯入user
//import com.example.demo.User;
import jakarta.persistence.*;

@Entity
@Table(name = "workflow_logs")
public class WorkflowLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

   @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator", nullable = false)
    private Long operator;
    //private User operator;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String remark;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public WorkflowLog() {}

}