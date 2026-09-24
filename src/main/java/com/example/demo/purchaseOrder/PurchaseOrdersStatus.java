package com.example.demo.purchaseOrder;

public enum PurchaseOrdersStatus {
    DRAFT,              // 草稿
    PENDING_APPROVAL,   // 待審核
    APPROVED,           // 已核准
    REJECTED,           // 已駁回
//    ORDERED,            // 已下單
    RECEIVED,           // 已全部到貨
    CANCELLED           // 已取消
}
