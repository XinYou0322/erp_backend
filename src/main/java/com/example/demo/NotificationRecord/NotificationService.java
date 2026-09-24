package com.example.demo.NotificationRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;

@Service
public class NotificationService {

    @Autowired
    private MaterialRepository materialRepository;

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

    @Transactional
    public void createLowStockAlert(Long userId, List<Map<String, Object>> materials) {
        if (userId == null || materials == null || materials.isEmpty()) {
            return;
        }

        // 巡迴檢查前端或 ERP 系統發送過來的所有低庫存物料
        for (Map<String, Object> item : materials) {
            // 1. 安全解析前端 DTO 傳遞過來的欄位
            String code = String.valueOf(item.getOrDefault("code", "unknown"));
            String name = String.valueOf(item.getOrDefault("name", "原物料"));

            // 安全轉換庫存數值 (相容 Integer, Double, Long, String 等型態)
            Object rawStock = item.getOrDefault("stock", 0);
            Object rawMinStock = item.getOrDefault("minStock", 0);
            double stock = rawStock instanceof Number ? ((Number) rawStock).doubleValue()
                    : Double.parseDouble(String.valueOf(rawStock));
            double minStock = rawMinStock instanceof Number ? ((Number) rawMinStock).doubleValue()
                    : Double.parseDouble(String.valueOf(rawMinStock));

            String unit = String.valueOf(item.getOrDefault("unit", "g"));

            // 2. 判斷是否為「總量完全歸零 (如 asd 品項)」的危急狀態
            boolean isCritical = (stock <= 0);

            // 3. 建立精緻的各別通知內容與標題
            String title = isCritical ? name + " 庫存緊急缺料" : name + " 庫存水位告急";

            // 格式化庫存數字，去掉結尾無用的 .0 (如 4000.0 g 變 4000 g)
            String stockStr = stock % 1 == 0 ? String.format("%.0f", stock) : String.valueOf(stock);
            String minStockStr = minStock % 1 == 0 ? String.format("%.0f", minStock) : String.valueOf(minStock);

            String content = String.format("【%s】當前可用庫存僅存 %s %s，已低於安全庫存警戒線 (%s %s)，建議立即安排採購。",
                    name, stockStr, unit, minStockStr, unit);

            String type = isCritical ? "danger" : "warning"; // 0庫存用紅色 danger，低於安全水位用 warning

            // 4. 🚀 呼叫現有的核心中樞方法：自動完成資料庫儲存，並獲取合法的 Long ID，同步透過 WebSocket 推播給前端
            createAndSendNotification(userId, title, content, "inventory", type, "/material");
        }
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
        wsPayload.put("unreadCount", notifRepository.countByUserIdAndReadFalse(userId));
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
        if (userId == null)
            return;

        try {
            // 1. 從資料庫撈出所有原物料項目
            List<?> allMaterials = materialRepository.findAll();
            boolean hasAlert = false;

            // 利用 Jackson ObjectMapper 將實體物件轉成 Map，直接繞過編譯時的 Getter 限制！
            tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();

            for (Object rawObj : allMaterials) {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = mapper.convertValue(rawObj, Map.class);

                // 2. 動態安全讀取物料名稱與計量單位
                String name = m.get("name") != null ? m.get("name").toString() : "未知物料";
                String unit = m.get("unit") != null ? m.get("unit").toString() : "g";

                // 3. 多重欄位名稱相容機制：目前庫存 (支援 stock, availableStock)
                double stock = 0.0;
                if (m.get("stock") != null) {
                    stock = Double.parseDouble(m.get("stock").toString());
                } else if (m.get("availableStock") != null) {
                    stock = Double.parseDouble(m.get("availableStock").toString());
                }

                // 4. 多重欄位名稱相容機制：安全庫存 (支援 safetyStock, minStock)
                double safetyStock = 0.0;
                if (m.get("safetyStock") != null) {
                    safetyStock = Double.parseDouble(m.get("safetyStock").toString());
                } else if (m.get("minStock") != null) {
                    safetyStock = Double.parseDouble(m.get("minStock").toString());
                }

                // 5. ⚖️ 動態判定：只要當前庫存低於安全庫存，就自動通報！
                if (stock < safetyStock) {
                    boolean isCritical = (stock <= 0);
                    String title = isCritical ? name + " 庫存緊急缺料" : name + " 庫存水位告急";

                    // 格式化數字去掉無用的 .0 (如 2500.0 變成 2500)
                    String stockStr = stock % 1 == 0 ? String.format("%.0f", stock) : String.valueOf(stock);
                    String safetyStockStr = safetyStock % 1 == 0 ? String.format("%.0f", safetyStock)
                            : String.valueOf(safetyStock);

                    String content = String.format("【%s】當前可用庫存僅存 %s %s，已低於安全庫存警戒線 (%s %s)，建議立即安排採購。",
                            name, stockStr, unit, safetyStockStr, unit);

                    String type = isCritical ? "danger" : "warning";

                    // 6. 🚀 呼叫核心中樞：自動寫入資料庫並即時推播
                    createAndSendNotification(userId, title, content, "inventory", type, "/material");
                    hasAlert = true;
                }
            }

            // 💡 降級防禦：如果目前沒有任何原物料低於安全水位，就發送正常狀態通報
            if (!hasAlert) {
                createAndSendNotification(userId, "庫存通報引擎自主檢測", "【系統通報】經全庫存即時掃描，目前全廠原物料均處於安全水位之上，無缺料風險。", "inventory",
                        "success", "/material");
            }

        } catch (Exception e) {
            System.err.println("動態掃描低庫存失敗，降級執行隨機模擬: " + e.getMessage());
            // 如果反射發生意外，自動降級執行原來的隨機抽樣通報，確保功能不崩潰
            String[][] samples = {
                    { "庫存告急", "生豆 [衣索比亞 耶加雪菲] 目前庫存僅剩 12kg！", "inventory", "danger", "/material" },
                    { "簽核審批待辦", "採購單 #PO-20260907001 待經理簽核審查。", "workflow", "warning", "/workflow/approvals" }
            };
            int idx = new java.util.Random().nextInt(samples.length);
            String[] pick = samples[idx];
            createAndSendNotification(userId, pick[0], pick[1], pick[2], pick[3], pick[4]);
        }
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
