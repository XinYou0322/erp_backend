package com.example.demo.ecpay;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.salesOrder.PaymentMethod;
import com.example.demo.salesOrder.SalesOrderRepository;
import com.example.demo.salesOrder.SalesOrderService;
import com.example.demo.salesOrder.SalesOrderStatus;
import com.example.demo.salesOrder.SalesOrders;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EcpayPaymentService {

    private static final ZoneId TAIPEI_ZONE = ZoneId.of("Asia/Taipei");
    private static final DateTimeFormatter ECPAY_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderService salesOrderService;

    @Value("${ecpay.payment-url}")
    private String paymentUrl;

    @Value("${ecpay.merchant-id}")
    private String merchantId;

    @Value("${ecpay.hash-key}")
    private String hashKey;

    @Value("${ecpay.hash-iv}")
    private String hashIv;

    @Value("${ecpay.backend-base-url}")
    private String backendBaseUrl;

    @Value("${ecpay.frontend-base-url}")
    private String frontendBaseUrl;

    // 【本次新增：ECPay 測試金流】依銷售單建立綠界 AioCheckOut/V5 POST 表單。
    @Transactional(readOnly = true)
    public EcpayCheckoutResponse createCheckout(Long salesOrderId) {
        SalesOrders order = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("找不到要付款的銷售單"));

        if (order.getStatus() != SalesOrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("只有待付款銷售單可以建立 ECPay 結帳");
        }

        String choosePayment = resolveChoosePayment(order.getPaymentMethod());
        int totalAmount = toEcpayAmount(order.getTotalAmount());

        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("MerchantID", merchantId);
        // 【本次修改：修正 ECPay 10300028】
        // 綠界交易編號每次送出都必須全域唯一，不再直接重用 ERP 銷售單號。
        fields.put("MerchantTradeNo", createMerchantTradeNo());
        fields.put("MerchantTradeDate", LocalDateTime.now(TAIPEI_ZONE).format(ECPAY_DATE_TIME));
        fields.put("PaymentType", "aio");
        fields.put("TotalAmount", String.valueOf(totalAmount));
        fields.put("TradeDesc", "POS Order");
        fields.put("ItemName", buildItemName(order));
        fields.put("ReturnURL", trimTrailingSlash(backendBaseUrl) + "/api/ecpay/payment-notify");
        fields.put("OrderResultURL", trimTrailingSlash(backendBaseUrl) + "/api/ecpay/order-result");
        fields.put("ClientBackURL", trimTrailingSlash(frontendBaseUrl)
                + "/pos?ecpay=cancel&orderNumber=" + urlEncodeQuery(order.getOrderNumber()));
        fields.put("ChoosePayment", choosePayment);
        fields.put("EncryptType", "1");
        fields.put("CustomField1", String.valueOf(order.getId()));
        fields.put("CustomField2", order.getOrderNumber());
        fields.put("CheckMacValue", createCheckMacValue(fields));

        return new EcpayCheckoutResponse(paymentUrl, fields, order.getOrderNumber());
    }

    // 【本次新增：ECPay 測試金流】驗證綠界回傳簽章、商店、訂單與金額，成功後才完成銷售單。
    public CallbackResult verifyAndProcessCallback(Map<String, String> callback) {
        validateCallbackSignature(callback);

        if (!merchantId.equals(callback.get("MerchantID"))) {
            throw new IllegalArgumentException("ECPay MerchantID 不符");
        }

        Long salesOrderId = parseSalesOrderId(callback.get("CustomField1"));
        BigDecimal tradeAmount = parseTradeAmount(callback.get("TradeAmt"));
        String orderNumber = callback.getOrDefault("CustomField2", "");

        // 綠界明確規範模擬付款通知不可更新訂單狀態；仍回覆成功以停止重送。
        if ("1".equals(callback.get("SimulatePaid"))) {
            return new CallbackResult(CallbackStatus.SIMULATED, orderNumber);
        }

        if ("1".equals(callback.get("RtnCode"))) {
            salesOrderService.completeEcpayPayment(salesOrderId, tradeAmount);
            return new CallbackResult(CallbackStatus.SUCCESS, orderNumber);
        }

        return new CallbackResult(CallbackStatus.FAILED, orderNumber);
    }

    public String buildFrontendResultUrl(CallbackResult result) {
        String resultValue = switch (result.status()) {
            case SUCCESS -> "success";
            case FAILED -> "failed";
            case SIMULATED -> "pending";
        };
        return trimTrailingSlash(frontendBaseUrl)
                + "/pos?ecpay=" + resultValue
                + "&orderNumber=" + urlEncodeQuery(result.orderNumber());
    }

    private String resolveChoosePayment(PaymentMethod paymentMethod) {
        if (paymentMethod == PaymentMethod.CREDIT_CARD) {
            return "Credit";
        }
        if (paymentMethod == PaymentMethod.MOBILE_PAYMENT) {
            return "TWQR";
        }
        throw new IllegalArgumentException("現金付款不應進入 ECPay 流程");
    }

    private int toEcpayAmount(BigDecimal amount) {
        try {
            int result = amount.intValueExact();
            if (result <= 0) {
                throw new IllegalArgumentException("ECPay 付款金額必須大於 0");
            }
            return result;
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("ECPay 新台幣付款金額必須是整數", exception);
        }
    }

    // 【本次修改：修正 ECPay 10300028】
    // P + UUID 前 19 碼固定為 20 碼英數字；每次付款嘗試都會取得新編號。
    // ERP 銷售單仍由 CustomField1、CustomField2 對應，不受隨機交易編號影響。
    private String createMerchantTradeNo() {
        String uniqueValue = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .toUpperCase(Locale.ROOT);
        return "P" + uniqueValue.substring(0, 19);
    }

    private String buildItemName(SalesOrders order) {
        String itemName = order.getItems().stream()
                .map(item -> sanitizeItemName(item.getProductName()))
                .collect(Collectors.joining("#"));
        if (itemName.isBlank()) {
            itemName = "POS Item";
        }
        return itemName.length() <= 400 ? itemName : itemName.substring(0, 400);
    }

    private String sanitizeItemName(String value) {
        return value == null ? "" : value.replaceAll("[#|&<>]", " ").trim();
    }

    private void validateCallbackSignature(Map<String, String> callback) {
        String received = callback.get("CheckMacValue");
        if (received == null || received.isBlank()) {
            throw new IllegalArgumentException("ECPay 回傳缺少 CheckMacValue");
        }

        Map<String, String> fields = new LinkedHashMap<>(callback);
        fields.remove("CheckMacValue");
        String expected = createCheckMacValue(fields);

        if (!MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII),
                received.toUpperCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII))) {
            throw new IllegalArgumentException("ECPay CheckMacValue 驗證失敗");
        }
    }

    private String createCheckMacValue(Map<String, String> fields) {
        Map<String, String> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sorted.putAll(fields);

        String parameters = sorted.entrySet().stream()
                .filter(entry -> !"CheckMacValue".equalsIgnoreCase(entry.getKey()))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String source = "HashKey=" + hashKey + "&" + parameters + "&HashIV=" + hashIv;
        String encoded = URLEncoder.encode(source, StandardCharsets.UTF_8)
                .replace("%21", "!")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2A", "*")
                .replace("%2D", "-")
                .replace("%2E", ".")
                .replace("%5F", "_")
                .toLowerCase(Locale.ROOT);

        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(encoded.getBytes(StandardCharsets.UTF_8));
            return toUpperHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("目前 Java 環境不支援 SHA-256", exception);
        }
    }

    private String toUpperHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format("%02X", value & 0xff));
        }
        return result.toString();
    }

    private Long parseSalesOrderId(String value) {
        try {
            return Long.valueOf(value);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("ECPay 回傳缺少有效的銷售單識別碼", exception);
        }
    }

    private BigDecimal parseTradeAmount(String value) {
        try {
            return new BigDecimal(value);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("ECPay 回傳金額格式不正確", exception);
        }
    }

    private String trimTrailingSlash(String value) {
        return value != null && value.endsWith("/")
                ? value.substring(0, value.length() - 1)
                : value;
    }

    private String urlEncodeQuery(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    public enum CallbackStatus {
        SUCCESS,
        FAILED,
        SIMULATED
    }

    public record CallbackResult(CallbackStatus status, String orderNumber) {
    }
}




