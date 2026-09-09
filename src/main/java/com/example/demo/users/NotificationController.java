package com.example.demo.users;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*") // 允許前端跨域呼叫
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 提取共用方法：從 request 獲取 userId，統一驗證邏輯
    private Long getValidatedUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = getValidatedUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @GetMapping
    public ResponseEntity<List<NotificationRecord>> getNotifications(
            HttpServletRequest request, // 改由 request 驗證身份
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyUnread,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Long userId = getValidatedUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity
                .ok(notificationService.getFilteredNotifications(userId, category, onlyUnread, page, size));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        // 單則已讀只需要通知 ID，但若要更安全，可以進 Service 驗證該通知是否屬於該 userId
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead(
            HttpServletRequest request, // 移除 payload 裡的 userId
            @RequestBody Map<String, String> payload) {

        Long userId = getValidatedUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        String category = payload.get("category");
        notificationService.markAllCategoryRead(userId, category);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearNotifications(
            HttpServletRequest request, // 移除 RequestParam 的 userId
            @RequestParam String category) {

        Long userId = getValidatedUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        notificationService.clearCategoryNotifications(userId, category);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/trigger-sample")
    public ResponseEntity<Void> triggerSample(@RequestBody Map<String, Long> payload) {
        // 模擬觸發測試通知，通常由系統後台或測試調用，保留傳入指定 userId
        Long userId = payload.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.triggerSampleAlert(userId);
        return ResponseEntity.ok().build();
    }
}
