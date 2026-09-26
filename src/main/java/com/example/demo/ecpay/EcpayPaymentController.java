package com.example.demo.ecpay;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.salesOrder.PaymentMethod;
import com.example.demo.salesOrder.SalesOrderCreDTO;
import com.example.demo.salesOrder.SalesOrderRespoDTO;
import com.example.demo.salesOrder.SalesOrderService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EcpayPaymentController {

    private final SalesOrderService salesOrderService;
    private final EcpayPaymentService ecpayPaymentService;

    // 【本次新增：ECPay 測試金流】先建立待付款銷售單，再回傳綠界測試環境表單。
    @PostMapping("/api/ecpay/checkout")
    public ResponseEntity<EcpayCheckoutResponse> createCheckout(
            @Valid @RequestBody SalesOrderCreDTO request,
            @SessionAttribute(name = "userId", required = false) Long loginUserId) {
        if (loginUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "請先登入");
        }
        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            throw new IllegalArgumentException("現金付款請使用原本結帳流程");
        }

        SalesOrderRespoDTO order = salesOrderService.createSalesOrder(request, loginUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ecpayPaymentService.createCheckout(order.getId()));
    }

    // 【本次新增：ECPay 測試金流】綠界伺服器背景通知；成功驗證後必須回覆 1|OK。
    @PostMapping(
            value = "/api/ecpay/payment-notify",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> paymentNotify(@RequestParam Map<String, String> callback) {
        try {
            ecpayPaymentService.verifyAndProcessCallback(callback);
            return ResponseEntity.ok("1|OK");
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body("0|" + exception.getMessage());
        }
    }

    // 【本次新增：ECPay 測試金流】付款頁瀏覽器回傳，同樣驗證後再導回 POS 顯示結果。
    @PostMapping(
            value = "/api/ecpay/order-result",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void orderResult(
            @RequestParam Map<String, String> callback,
            HttpServletResponse response) throws IOException {
        try {
            EcpayPaymentService.CallbackResult result =
                    ecpayPaymentService.verifyAndProcessCallback(callback);
            response.sendRedirect(ecpayPaymentService.buildFrontendResultUrl(result));
        } catch (RuntimeException exception) {
            // 【本次新增：ECPay 測試金流】失敗也導回設定的前端網址，避免被送到後端 8080 的 /pos。
            response.sendRedirect(ecpayPaymentService.buildFrontendResultUrl(
                    new EcpayPaymentService.CallbackResult(
                            EcpayPaymentService.CallbackStatus.FAILED, "")));
        }
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<Map<String, String>> handleBusinessError(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
}

