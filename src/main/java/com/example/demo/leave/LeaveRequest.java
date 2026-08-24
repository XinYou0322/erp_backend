package com.example.demo.leave;

import java.time.LocalDateTime;
//匯入user
//import com.example.demo.User;
import jakarta.persistence.*;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Long user;
    //private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String remark;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    // Getter & Setter
}
