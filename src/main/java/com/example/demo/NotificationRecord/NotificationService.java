package com.example.demo.NotificationRecord;

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
        return notifRepository.countVisibleUnread(userId);
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

        NotificationRecord saved = notifRepository.save(record);

        // 包裝成與前端通訊的即時資料包
        Map<String, Object> wsPayload = new HashMap<>();
        wsPayload.put("action", "NEW_NOTIFICATION");
        wsPayload.put("unreadCount", notifRepository.countVisibleUnread(userId));
        wsPayload.put("notification", saved);

        // 執行即時推播
        webSocketHandler.sendToUser(userId, wsPayload);
    }

    public java.util.Optional<NotificationRecord> getById(Long id) {
        return notifRepository.findById(id);
    }

    /**
     * 對接前端測試按鈕: triggerSampleAlert
     */

    public void triggerSampleAlert(Long userId) {
        String[][] samples = {
                { "通知功能測試", "這是一則通知中心測試訊息。", "system", "info", "/dashboard" },
                { "簽核審批待辦", "目前有一筆流程等待處理。", "workflow", "warning", "/workflows" },
                { "採購供鏈通知", "請確認近期採購單的預計到貨日期。", "supplier", "info", "/purchaseOrder" }
        };
        String[] sample = samples[new Random().nextInt(samples.length)];
        createAndSendNotification(userId, sample[0], sample[1], sample[2], sample[3], sample[4]);
    }
    /**
     * 3. 建立打卡簽到/簽退通知
     * 歸類在 security (資安考勤)，跳轉至 /attendance
     */
    @Transactional
    public void createClockAlert(Long userId, String type, String userName, String time) {
        String title = "IN".equalsIgnoreCase(type) ? "員工簽到成功" : "員工簽退成功";
        String alertType = "IN".equalsIgnoreCase(type) ? "success" : "info";
        String content = "IN".equalsIgnoreCase(type)
                ? String.format("同仁【%s】已於 %s 完成今日上班打卡簽到。", userName, time)
                : String.format("同仁【%s】已於 %s 完成今日下班打卡簽退。", userName, time);

        // 呼叫核心中樞，自動處理儲存與 WebSocket 推播
        createAndSendNotification(userId, title, content, "security", alertType, "/attendance");
    }

    /**
     * 4. 建立原物料變動通知 (新增/進貨)
     * 新增歸類在 inventory (庫存物料)，進貨歸類在 supplier (採購供鏈)
     */
    @Transactional
    public void createMaterialAlert(Long userId, String eventType, String materialName, Map<String, Object> payload) {
        if ("CREATE".equalsIgnoreCase(eventType)) {
            String creator = (String) payload.getOrDefault("creator", "管理員");
            String content = String.format("由【%s】新增了原物料：%s，已建檔至物料清單。", creator, materialName);

            // 新增原物料：跳轉至 /material
            createAndSendNotification(userId, "成功建立新原物料項目", content, "inventory", "success", "/material");

        } else if ("IMPORT".equalsIgnoreCase(eventType)) {
            String quantity = String.valueOf(payload.getOrDefault("quantity", "0"));
            String batchNo = (String) payload.getOrDefault("batchNo", "N/A");
            String content = String.format("原物料【%s】已成功進貨入庫 %s kg！批號：%s。", materialName, quantity, batchNo);

            // 進貨入庫：跳轉至 /bom
            createAndSendNotification(userId, "原物料進貨入庫通知", content, "supplier", "success", "/bom");
        }
    }
}
