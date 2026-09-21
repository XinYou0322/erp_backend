package com.example.demo.NotificationRecord;

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
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private Long normalizeUserId(Object rawUserId) {
        if (rawUserId == null) {
            return null;
        }
        if (rawUserId instanceof Number numberValue) {
            long userId = numberValue.longValue();
            return userId >= 0 ? userId : null;
        }
        if (rawUserId instanceof String stringValue) {
            String trimmed = stringValue.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                long parsed = Long.parseLong(trimmed);
                return parsed >= 0 ? parsed : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 提取共用方法：支援從 Session 或 Request Header 取得 userId
    private Long getValidatedUserId(HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            Long sessionUserId = normalizeUserId(session.getAttribute("userId"));
            if (sessionUserId != null) {
                return sessionUserId;
            }
        }

        String headerUserId = request.getHeader("X-User-Id");
        if (headerUserId != null && !headerUserId.isBlank()) {
            return normalizeUserId(headerUserId);
        }
        return null;
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
    public ResponseEntity<Void> markAsRead(HttpServletRequest request, @PathVariable Long id) {
        Long userId = getValidatedUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        NotificationRecord notification = notificationService.getById(id)
                .orElse(null);
        if (notification == null || !userId.equals(notification.getUserId())) {
            return ResponseEntity.status(403).build();
        }

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
    public ResponseEntity<Void> triggerSample(@RequestBody Map<String, Object> payload) {
        Long userId = normalizeUserId(payload != null ? payload.get("userId") : null);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.triggerSampleAlert(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/low-stock")
    public ResponseEntity<Void> createLowStockNotification(
            HttpServletRequest request,
            @RequestBody Map<String, Object> payload) {

        Long userId = getValidatedUserId(request);
        if (userId == null) {
            Object candidate = payload.get("userId");
            if (candidate instanceof Number) {
                userId = ((Number) candidate).longValue();
            }
        }

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        Object materialsObj = payload.get("materials");
        if (!(materialsObj instanceof java.util.List<?> materials)) {
            return ResponseEntity.badRequest().build();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> materialList = (List<Map<String, Object>>) materials;
        notificationService.createLowStockAlert(userId, materialList);
        return ResponseEntity.ok().build();
    }
}
