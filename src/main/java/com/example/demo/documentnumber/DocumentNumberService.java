package com.example.demo.documentnumber;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentNumberService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Taipei");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final int LOCK_TIMEOUT_MILLISECONDS = 10_000;

    private final JdbcTemplate jdbcTemplate;

    public DocumentNumberService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 【本次新增：共用取號模組】
     * 產生 PO-yyyyMMdd-0001 或 SO-yyyyMMdd-0001 格式的正式單號。
     * 必須由建立單據的交易呼叫，確保應用程式鎖直到單據完成儲存後才釋放。
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public String nextNumber(DocumentNumberType type) {
        if (type == null) {
            throw new IllegalArgumentException("單據類型不得為空");
        }

        String dateText = LocalDate.now(BUSINESS_ZONE).format(DATE_FORMAT);
        String numberPrefix = type.getPrefix() + "-" + dateText + "-";

        acquireTransactionLock(type, dateText);

        long currentSequence = findCurrentSequence(type, numberPrefix);
        long nextSequence = Math.addExact(currentSequence, 1L);

        return numberPrefix + String.format("%04d", nextSequence);
    }

    /**
     * 使用 SQL Server sp_getapplock，將「單據種類＋日期」當成鎖定資源。
     * 同一天同類型的單據只能依序取號，避免多人同時新增時取得相同號碼。
     */
    private void acquireTransactionLock(DocumentNumberType type, String dateText) {
        String lockResource = "ERP:DOCUMENT_NUMBER:" + type.name() + ":" + dateText;

        Integer lockResult = jdbcTemplate.queryForObject("""
                SET NOCOUNT ON;
                DECLARE @result int;
                EXEC @result = sys.sp_getapplock
                    @Resource = ?,
                    @LockMode = 'Exclusive',
                    @LockOwner = 'Transaction',
                    @LockTimeout = ?;
                SELECT @result;
                """,
                Integer.class,
                lockResource,
                LOCK_TIMEOUT_MILLISECONDS);

        // SQL Server 回傳 0 或正數代表成功，負數代表逾時、取消或鎖定失敗。
        if (lockResult == null || lockResult < 0) {
            throw new IllegalStateException("單據取號忙碌中，請稍後再試");
        }
    }

    private long findCurrentSequence(DocumentNumberType type, String numberPrefix) {
        String sql = switch (type) {
            case PURCHASE_ORDER -> """
                    SELECT COALESCE(
                        MAX(TRY_CONVERT(BIGINT, SUBSTRING(order_number, ?, 40))),
                        0
                    )
                    FROM dbo.purchase_orders WITH (UPDLOCK, HOLDLOCK)
                    WHERE order_number LIKE ?
                    """;
            case SALES_ORDER -> """
                    SELECT COALESCE(
                        MAX(TRY_CONVERT(BIGINT, SUBSTRING(order_number, ?, 40))),
                        0
                    )
                    FROM dbo.sales_orders WITH (UPDLOCK, HOLDLOCK)
                    WHERE order_number LIKE ?
                    """;
        };

        Long current = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                numberPrefix.length() + 1,
                numberPrefix + "%");

        return current == null ? 0L : current;
    }
}
