package com.example.demo.systemsetting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemSettingKey {
    PURCHASE_ORDER_RECEIVING_ENABLED(
            "false",
            "是否啟用採購單收貨入庫",
            ValueType.BOOLEAN),
    RETAIL_MODE_ENABLED(
            "false",
            "是否啟用零售商品模式",
            ValueType.BOOLEAN),
    POS_AUTO_MATERIAL_DEDUCTION_ENABLED(
            "false",
            "POS 結帳時是否依商品 BOM 自動扣除原物料",
            ValueType.BOOLEAN),
    SITE_NAME(
            "深淵之流",
            "顯示於側邊欄與瀏覽器標題的網站名稱",
            ValueType.STRING),
    SITE_LOGO_URL(
            "",
            "顯示於側邊欄與瀏覽器頁籤的網站圖示",
            ValueType.STRING);
            ValueType.BOOLEAN),
    SALES_INVENTORY_SYNC_ENABLED(
            "false",
            "是否在銷售完成時依 BOM 同步扣除庫存",
            ValueType.BOOLEAN);

    private final String defaultValue;
    private final String description;
    private final ValueType valueType;

    public enum ValueType {
        BOOLEAN,
        STRING
    }
}
