package com.example.demo.workflow.entity;

import java.time.LocalDateTime;
import com.example.demo.workflow.enums.DocumentType;
//匯入user
//import com.example.demo.User;
import jakarta.persistence.*;

@Entity
@Table(name = "workflows")
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(nullable = false, length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant", nullable = false)
    private Long applicant;
    //private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver", nullable = false)
    private Long approver;
    //private User approver;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


  
}