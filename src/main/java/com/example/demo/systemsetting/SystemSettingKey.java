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
            ValueType.BOOLEAN);

    private final String defaultValue;
    private final String description;
    private final ValueType valueType;

    public enum ValueType {
        BOOLEAN
    }
}
