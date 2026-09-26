package com.example.demo.salesOrder;

public enum SalesOrderStatus {
    // 【本次新增：ECPay 測試金流】電子支付建立訂單後，尚未收到 ECPay 付款成功通知。
    PENDING_PAYMENT,
    COMPLETED, //交易已完成
    VOIDED //交易已作廢
}

