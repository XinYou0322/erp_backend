package com.example.demo.users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notifRepository;

    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    public int getUnreadCount(Long userId) {
        return notifRepository.countByUserIdAndReadFalse(userId);
    }

    public List<NotificationRecord> getFilteredNotifications(Long userId, String category, Boolean onlyUnread, int page,
            int size) {
        boolean unreadFlag = onlyUnread != null && onlyUnread;
        return notifRepository.findFilteredNotifications(userId, category, unreadFlag, PageRequest.of(page, size));
    }

    @Transactional
    public void markAsRead(Long id) {
        notifRepository.findById(id).ifPresent(notif -> {
            notif.setRead(true);
            notifRepository.save(notif);
        });
    }

    @Transactional
    public void markAllCategoryRead(Long userId, String category) {
        notifRepository.markAllAsRead(userId, category);
    }

    @Transactional
    public void clearCategoryNotifications(Long userId, String category) {
        notifRepository.clearNotifications(userId, category);
    }

    /**
     * 核心中樞：當 ERP 發生任何業務事件時，呼叫此方法寫入 DB 並即時推播
     */
    @Transactional
    public void createAndSendNotification(Long userId, String title, String content, String category, String type,
            String actionRoute) {
        NotificationRecord record = new NotificationRecord();
        record.setUserId(userId);
        record.setTitle(title);
        record.setContent(content);
        record.setCategory(category);
        record.setType(type);
        record.setActionRoute(actionRoute);
        notifRepository.save(record);

        notifRepository.saveAndFlush(record);

        // 包裝成與前端通訊的即時資料包
        Map<String, Object> wsPayload = new HashMap<>();
        wsPayload.put("action", "NEW_NOTIFICATION");
        wsPayload.put("unreadCount", notifRepository.countByUserIdAndReadFalse(userId));
        wsPayload.put("notification", record);

        // 執行即時推播
        webSocketHandler.sendToUser(userId, wsPayload);
    }

    /**
     * 對接前端測試按鈕: triggerSampleAlert
     */
    public void triggerSampleAlert(Long userId) {
        String[][] samples = {
                { "庫存告急", "生豆 [衣索比亞 耶加雪菲] 目前庫存僅剩 12kg！", "inventory", "danger", "/inventory/materials" },
                { "簽核審批待辦", "採購單 #PO-20260907001 待經理簽核審查。", "workflow", "warning", "/workflow/approvals" },
                { "採購供鏈通知", "供應商「大宗生豆進口商」已建立出貨單。", "supplier", "info", "/supplier/shipments" },
                { "資安稽核警告", "帳號於非正常辦公時段嘗試匯出客戶清單。", "security", "danger", "/security/audit-logs" }
        };
        int idx = new Random().nextInt(samples.length);
        String[] pick = samples[idx];

        createAndSendNotification(userId, pick[0], pick[1], pick[2], pick[3], pick[4]);
    }
}
