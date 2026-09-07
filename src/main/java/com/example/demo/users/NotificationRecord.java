package com.example.demo.users;

import java.time.Instant;

import org.hibernate.annotations.Collate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@Table(name = "notification_record")
@NoArgsConstructor
public class NotificationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id, nullable = false")
    private Long userId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 30)
    private String catecory; // inventory(庫存), workflow(工作流程), supplier(供應商), security(安全)

    @Column(nullable = false, length = 20)
    private String type; // info(資訊), success(成功), warning(警告), danger(危險/錯誤)

    @Column(name = "action_route")
    private String actionRoute;

    @Column(name = "is_read")
    private Boolean isRade = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
