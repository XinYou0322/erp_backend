package com.example.demo.documentnumber;

/**
 * 【本次新增：共用取號模組】
 * 集中管理各單據的號碼前綴，後續若增加退貨單或調撥單可在此擴充。
 */
public enum DocumentNumberType {

    PURCHASE_ORDER("PO"),
    SALES_ORDER("SO");

    private final String prefix;

    DocumentNumberType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
