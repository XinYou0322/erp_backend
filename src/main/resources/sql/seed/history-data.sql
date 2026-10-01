/* =============================================================
   飲料店 ERP - 可選擇執行的歷史分析資料

   使用方式：
   1. 先建立 data.sql 的基礎資料。
   2. 修改下方四個參數。
   3. 使用 SQL Server Management Studio 手動執行。

   特性：
   - 每個月份獨立提交，避免單一巨大 Transaction。
   - 使用固定歷史單號，相同月份可安全重跑。
   - 不刪除使用者建立的正式資料。
   ============================================================= */
SET NOCOUNT ON;
SET XACT_ABORT ON;

/* ---------- 可調整參數 ---------- */
DECLARE @StartMonth date = DATEADD(MONTH, -11, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1));
DECLARE @EndMonth date = DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1);
DECLARE @OrdersPerMonth int = 300;
DECLARE @SeedBatch nvarchar(50) = N'HISTORY-DEFAULT';

/* ---------- 參數與基礎資料檢查 ---------- */
SET @StartMonth = DATEFROMPARTS(YEAR(@StartMonth), MONTH(@StartMonth), 1);
SET @EndMonth = DATEFROMPARTS(YEAR(@EndMonth), MONTH(@EndMonth), 1);

IF @StartMonth > @EndMonth
    THROW 51001, N'歷史資料開始月份不可晚於結束月份。', 1;

IF @EndMonth > DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)
    THROW 51002, N'歷史資料不可建立到未來月份。', 1;

IF @OrdersPerMonth < 1 OR @OrdersPerMonth > 5000
    THROW 51003, N'每月訂單數必須介於 1 到 5000。', 1;

IF OBJECT_ID(N'dbo.sales_orders', N'U') IS NULL
   OR OBJECT_ID(N'dbo.sales_order_items', N'U') IS NULL
   OR OBJECT_ID(N'dbo.purchase_orders', N'U') IS NULL
    THROW 51004, N'找不到交易資料表，請先建立基礎資料。', 1;

DECLARE @NowLocal datetime2 = SYSDATETIME();
DECLARE @HistoryCreator bigint = (SELECT id FROM users WHERE username = 'staff01');
DECLARE @HistoryApprover bigint = (SELECT id FROM users WHERE username = 'store_manager01');
DECLARE @HistoryReceiver bigint = COALESCE(
    (SELECT id FROM users WHERE username = 'staff02'),
    @HistoryCreator
);
DECLARE @HistorySupplier bigint = (SELECT MIN(id) FROM suppliers WHERE status = 'ACTIVE');

IF @HistoryCreator IS NULL OR @HistoryApprover IS NULL OR @HistorySupplier IS NULL
    THROW 51005, N'缺少歷史資料需要的測試使用者或供應商。', 1;

DECLARE @HistoryProducts TABLE
(
    product_no int PRIMARY KEY,
    product_id bigint NOT NULL,
    sku varchar(50) NOT NULL,
    product_name nvarchar(100) NOT NULL,
    price decimal(18,2) NOT NULL,
    cost decimal(18,2) NOT NULL
);

INSERT INTO @HistoryProducts
    (product_no, product_id, sku, product_name, price, cost)
SELECT
    ROW_NUMBER() OVER (ORDER BY id),
    id,
    sku,
    name,
    selling_price,
    COALESCE(cost_price, selling_price * 0.4)
FROM products
WHERE status = 'ACTIVE'
  AND selling_price > 0;

DECLARE @HistoryProductCount int = (SELECT COUNT(*) FROM @HistoryProducts);
IF @HistoryProductCount = 0
    THROW 51006, N'缺少可銷售商品。', 1;

DECLARE @Numbers TABLE (n int PRIMARY KEY);
;WITH Digits AS
(
    SELECT n FROM (VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9)) value(n)
)
INSERT INTO @Numbers (n)
SELECT TOP (@OrdersPerMonth)
    1 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n
FROM Digits ones
CROSS JOIN Digits tens
CROSS JOIN Digits hundreds
CROSS JOIN Digits thousands
ORDER BY 1;

DECLARE @MonthSales TABLE
(
    order_number varchar(40) PRIMARY KEY,
    created_at datetime2 NOT NULL,
    product_id bigint NOT NULL,
    sku varchar(50) NOT NULL,
    product_name nvarchar(100) NOT NULL,
    quantity decimal(18,4) NOT NULL,
    unit_price decimal(18,2) NOT NULL,
    unit_cost decimal(18,2) NOT NULL,
    subtotal decimal(18,2) NOT NULL,
    payment_method varchar(30) NOT NULL
);

DECLARE @HistoryMonth date = @StartMonth;
DECLARE @NextMonth date;
DECLARE @MonthCutoff datetime2;
DECLARE @AvailableSeconds int;
DECLARE @HistoryPurchaseIndex int;
DECLARE @HistoryPurchaseDay date;
DECLARE @HistoryPurchaseTime datetime2;
DECLARE @HistoryPurchaseNumber varchar(40);
DECLARE @HistoryPurchaseId bigint;
DECLARE @HistoryWorkflowId bigint;
DECLARE @Progress nvarchar(200);

WHILE @HistoryMonth <= @EndMonth
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION;

        SET @NextMonth = DATEADD(MONTH, 1, @HistoryMonth);
        SET @MonthCutoff = CASE
            WHEN @NextMonth <= CAST(@NowLocal AS date) THEN CAST(@NextMonth AS datetime2)
            ELSE @NowLocal
        END;
        SET @AvailableSeconds = DATEDIFF(SECOND, CAST(@HistoryMonth AS datetime2), @MonthCutoff);
        IF @AvailableSeconds < 1 SET @AvailableSeconds = 1;

        DELETE FROM @MonthSales;

        INSERT INTO @MonthSales
            (order_number, created_at, product_id, sku, product_name,
             quantity, unit_price, unit_cost, subtotal, payment_method)
        SELECT
            CONCAT('HIST-SO-', CONVERT(char(6), @HistoryMonth, 112), '-',
                   RIGHT('0000' + CONVERT(varchar(4), numbers.n), 4)),
            DATEADD(
                SECOND,
                CAST(ABS(CAST(CHECKSUM(CONCAT(CONVERT(char(6), @HistoryMonth, 112), '-', numbers.n)) AS bigint))
                     % @AvailableSeconds AS int),
                CAST(@HistoryMonth AS datetime2)
            ),
            product.product_id,
            product.sku,
            product.product_name,
            CAST(1 + ((numbers.n * 7) % 5) AS decimal(18,4)),
            product.price,
            product.cost,
            product.price * (1 + ((numbers.n * 7) % 5)),
            CASE numbers.n % 3
                WHEN 0 THEN 'CASH'
                WHEN 1 THEN 'CREDIT_CARD'
                ELSE 'MOBILE_PAYMENT'
            END
        FROM @Numbers numbers
        INNER JOIN @HistoryProducts product
            ON product.product_no = ((numbers.n - 1) % @HistoryProductCount) + 1;

        INSERT INTO sales_orders
            (order_number, status, payment_method, total_amount,
             created_by_user_id, created_at, note)
        SELECT
            seed.order_number,
            'COMPLETED',
            seed.payment_method,
            seed.subtotal,
            @HistoryCreator,
            seed.created_at,
            CONCAT(N'[歷史假資料:', @SeedBatch, N'] 銷售分析資料')
        FROM @MonthSales seed
        WHERE NOT EXISTS
        (
            SELECT 1
            FROM sales_orders existing
            WHERE existing.order_number = seed.order_number
        );

        INSERT INTO sales_order_items
            (sales_order_id, product_id, product_sku, product_name,
             quantity, unit_price, unit_cost, subtotal)
        SELECT
            orders.id,
            seed.product_id,
            seed.sku,
            seed.product_name,
            seed.quantity,
            seed.unit_price,
            seed.unit_cost,
            seed.subtotal
        FROM @MonthSales seed
        INNER JOIN sales_orders orders
            ON orders.order_number = seed.order_number
        WHERE NOT EXISTS
        (
            SELECT 1
            FROM sales_order_items existing
            WHERE existing.sales_order_id = orders.id
        );

        /* 每月四筆已收貨採購，不列入目前待到貨量。 */
        SET @HistoryPurchaseIndex = 0;
        WHILE @HistoryPurchaseIndex < 4
        BEGIN
            SET @HistoryPurchaseDay = DATEADD(DAY, @HistoryPurchaseIndex * 7, @HistoryMonth);
            SET @HistoryPurchaseTime = DATEADD(HOUR, 9, CAST(@HistoryPurchaseDay AS datetime2));
            SET @HistoryPurchaseNumber = CONCAT(
                'HIST-PO-', CONVERT(char(6), @HistoryMonth, 112), '-',
                RIGHT('00' + CONVERT(varchar(2), @HistoryPurchaseIndex + 1), 2)
            );

            IF @HistoryPurchaseTime <= @NowLocal
            BEGIN
                IF NOT EXISTS
                (
                    SELECT 1 FROM purchase_orders WHERE order_number = @HistoryPurchaseNumber
                )
                BEGIN
                    INSERT INTO purchase_orders
                        (order_number, supplier_id, status, created_by_user_id,
                         approved_by_user_id, received_by_user_id, total,
                         created_at, updated_at, expected_delivery_date,
                         received_at, decision_remark)
                    VALUES
                        (@HistoryPurchaseNumber, @HistorySupplier, 'RECEIVED',
                         @HistoryCreator, @HistoryApprover, @HistoryReceiver, 0,
                         @HistoryPurchaseTime, @HistoryPurchaseTime,
                         @HistoryPurchaseDay, @HistoryPurchaseTime,
                         CONCAT(N'[歷史假資料:', @SeedBatch, N'] 定期原料採購'));
                END;

                SET @HistoryPurchaseId = (
                    SELECT id FROM purchase_orders WHERE order_number = @HistoryPurchaseNumber
                );

                IF NOT EXISTS
                (
                    SELECT 1 FROM purchase_order_items WHERE purchase_order_id = @HistoryPurchaseId
                )
                BEGIN
                    INSERT INTO purchase_order_items
                        (purchase_order_id, material_id, quantity, price)
                    SELECT TOP (3)
                        @HistoryPurchaseId,
                        material.id,
                        10 + ((material.id + @HistoryPurchaseIndex) % 21),
                        material.purchase_cost
                    FROM materials material
                    WHERE material.status = 'ACTIVE'
                      AND material.purchase_cost IS NOT NULL
                      AND material.purchase_cost > 0
                    ORDER BY ((material.id + @HistoryPurchaseIndex * 3) % 17), material.id;
                END;

                UPDATE purchase_orders
                SET total = COALESCE(
                    (SELECT SUM(quantity * price)
                     FROM purchase_order_items
                     WHERE purchase_order_id = @HistoryPurchaseId),
                    0
                )
                WHERE id = @HistoryPurchaseId;

                IF NOT EXISTS
                (
                    SELECT 1 FROM workflows
                    WHERE document_type = 'ORDER'
                      AND document_id = @HistoryPurchaseId
                )
                BEGIN
                    INSERT INTO workflows
                        (document_type, document_id, status,
                         applicant_id, approver_id, created_at)
                    VALUES
                        ('ORDER', @HistoryPurchaseId, 'APPROVED',
                         @HistoryCreator, @HistoryApprover, @HistoryPurchaseTime);
                    SET @HistoryWorkflowId = SCOPE_IDENTITY();
                END
                ELSE
                BEGIN
                    SET @HistoryWorkflowId = (
                        SELECT TOP (1) id FROM workflows
                        WHERE document_type = 'ORDER'
                          AND document_id = @HistoryPurchaseId
                        ORDER BY created_at, id
                    );
                END;

                INSERT INTO workflow_logs
                    (workflow_id, action, operator_id, remark, created_at)
                SELECT
                    @HistoryWorkflowId,
                    action_seed.action,
                    CASE WHEN action_seed.action = 'SUBMIT'
                         THEN @HistoryCreator ELSE @HistoryApprover END,
                    CONCAT(N'[歷史假資料:', @SeedBatch, N'] 採購簽核'),
                    DATEADD(MINUTE, action_seed.minute_offset, @HistoryPurchaseTime)
                FROM (VALUES ('SUBMIT', 0), ('APPROVE', 5)) action_seed(action, minute_offset)
                WHERE NOT EXISTS
                (
                    SELECT 1 FROM workflow_logs existing
                    WHERE existing.workflow_id = @HistoryWorkflowId
                      AND existing.action = action_seed.action
                );
            END;

            SET @HistoryPurchaseIndex += 1;
        END;

        COMMIT TRANSACTION;

        SET @Progress = CONCAT(
            N'已完成 ', CONVERT(char(7), @HistoryMonth, 120), N'：',
            @OrdersPerMonth, N' 張歷史銷售單'
        );
        RAISERROR(@Progress, 10, 1) WITH NOWAIT;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;

    SET @HistoryMonth = DATEADD(MONTH, 1, @HistoryMonth);
END;

SELECT
    CONVERT(char(7), created_at, 120) AS history_month,
    COUNT(*) AS order_count,
    SUM(total_amount) AS revenue
FROM sales_orders
WHERE order_number LIKE 'HIST-SO-%'
  AND created_at >= @StartMonth
  AND created_at < DATEADD(MONTH, 1, @EndMonth)
GROUP BY CONVERT(char(7), created_at, 120)
ORDER BY history_month;
GO
