package com.example.demo.systemsetting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemSettingKey {
    PURCHASE_ORDER_RECEIVING_ENABLED(
            "false",
            "是否啟用採購單收貨入庫",
            ValueType.BOOLEAN);

    private final String defaultValue;
    private final String description;
    private final ValueType valueType;

    public enum ValueType {
        BOOLEAN
    }
}
