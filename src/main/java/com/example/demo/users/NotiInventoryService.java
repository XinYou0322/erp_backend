package com.example.demo.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void deductMaterialStock(Long materialId, int amount, Long operatorId) {
        // 1. 執行扣減庫存邏輯 ...
        int currentStock = 8; // 假設扣減後只剩 8 件
        int alertThreshold = 10;
        String materialName = "衣索比亞 耶加雪菲";

        // 2. 自動判斷並發送通知
        if (currentStock < alertThreshold) {
            notificationService.createAndSendNotification(
                    operatorId,
                    "庫存告急",
                    String.format("原物料 [%s] 目前庫存僅剩 %d 包，低於安全水位！", materialName, currentStock),
                    "inventory", // 分類對接前端標籤
                    "danger", // 提示等級顯示紅色
                    "/inventory/materials" // 點擊直接導航至物料管理頁
            );
        }
    }
}
