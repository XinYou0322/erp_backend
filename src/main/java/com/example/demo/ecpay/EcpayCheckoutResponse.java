package com.example.demo.ecpay;

import java.util.Map;

// 【本次新增：ECPay 測試金流】回傳前端要 POST 到綠界的網址與完整表單欄位。
public record EcpayCheckoutResponse(
        String actionUrl,
        Map<String, String> formFields,
        String orderNumber) {
}

