package com.example.demo.workflow.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import com.example.demo.workflow.enums.WorkflowAction;
import com.example.demo.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
    @Column(name = "action", nullable = false)
    private WorkflowAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private User operator;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    private Instant createAt;

    @PrePersist
    public void onCreate() {
        if (createAt == null) {
            createAt = Instant.now();
        }
    }

}