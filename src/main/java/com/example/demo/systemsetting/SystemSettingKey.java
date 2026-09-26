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
    SALES_INVENTORY_SYNC_ENABLED(
            "false",
            "是否在銷售完成時依 BOM 同步扣除庫存",
            ValueType.BOOLEAN);

    private final String defaultValue;
    private final String description;
    private final ValueType valueType;

    public enum ValueType {
        BOOLEAN
    }
}
