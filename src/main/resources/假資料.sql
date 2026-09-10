/*roles*/
INSERT INTO roles ([description],[name])
VALUES
(NULL,'店長'),
(NULL,'副店長'),
(NULL,'經理'),
(NULL,'組長'),
(NULL,'副組長'),
(NULL,'班長'),
(NULL,'正職'),
(NULL,'PT');
/*users*/
--所有測試帳號密碼都是：Test1234!
INSERT INTO [users]
(
    [username],
    [password],
    [name],
    [email],
    [role_id],
    [status],
    [created_at]
)
VALUES
-- 店長
(
    'store_manager01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'陳志明',
    'store.manager01@example.com',
    1,
    'ACTIVE',
    DATEADD(DAY, -120, SYSUTCDATETIME())
),

-- 副店長
(
    'deputy_manager01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'林雅雯',
    'deputy.manager01@example.com',
    2,
    'ACTIVE',
    DATEADD(DAY, -110, SYSUTCDATETIME())
),

-- 經理
(
    'purchase_manager01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'王建國',
    'purchase.manager01@example.com',
    3,
    'ACTIVE',
    DATEADD(DAY, -100, SYSUTCDATETIME())
),
(
    'sales_manager01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'張淑芬',
    'sales.manager01@example.com',
    3,
    'ACTIVE',
    DATEADD(DAY, -95, SYSUTCDATETIME())
),

-- 組長
(
    'team_leader01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'黃俊傑',
    'team.leader01@example.com',
    4,
    'ACTIVE',
    DATEADD(DAY, -90, SYSUTCDATETIME())
),
(
    'team_leader02',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'李佩珊',
    'team.leader02@example.com',
    4,
    'ACTIVE',
    DATEADD(DAY, -85, SYSUTCDATETIME())
),

-- 副組長
(
    'assistant_leader01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'吳柏翰',
    'assistant.leader01@example.com',
    5,
    'ACTIVE',
    DATEADD(DAY, -80, SYSUTCDATETIME())
),
(
    'assistant_leader02',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'蔡宜庭',
    'assistant.leader02@example.com',
    5,
    'ACTIVE',
    DATEADD(DAY, -75, SYSUTCDATETIME())
),

-- 班長
(
    'shift_leader01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'劉冠廷',
    'shift.leader01@example.com',
    6,
    'ACTIVE',
    DATEADD(DAY, -70, SYSUTCDATETIME())
),
(
    'shift_leader02',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'鄭佳蓉',
    'shift.leader02@example.com',
    6,
    'ACTIVE',
    DATEADD(DAY, -65, SYSUTCDATETIME())
),

-- 正職
(
    'staff01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'王小明',
    'staff01@example.com',
    7,
    'ACTIVE',
    DATEADD(DAY, -60, SYSUTCDATETIME())
),
(
    'staff02',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'李佳穎',
    'staff02@example.com',
    7,
    'ACTIVE',
    DATEADD(DAY, -55, SYSUTCDATETIME())
),
(
    'staff03',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'張志豪',
    'staff03@example.com',
    7,
    'ACTIVE',
    DATEADD(DAY, -50, SYSUTCDATETIME())
),
(
    'staff04',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'黃雅婷',
    'staff04@example.com',
    7,
    'ACTIVE',
    DATEADD(DAY, -45, SYSUTCDATETIME())
),
(
    'staff05',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'陳柏宇',
    'staff05@example.com',
    7,
    'ACTIVE',
    DATEADD(DAY, -40, SYSUTCDATETIME())
),
(
    'staff06',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'林佩芸',
    'staff06@example.com',
    7,
    'ACTIVE',
    DATEADD(DAY, -35, SYSUTCDATETIME())
),

-- PT
(
    'pt01',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'周子晴',
    'pt01@example.com',
    8,
    'ACTIVE',
    DATEADD(DAY, -30, SYSUTCDATETIME())
),
(
    'pt02',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'許家豪',
    'pt02@example.com',
    8,
    'ACTIVE',
    DATEADD(DAY, -25, SYSUTCDATETIME())
),
(
    'pt03',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'郭欣怡',
    'pt03@example.com',
    8,
    'ACTIVE',
    DATEADD(DAY, -20, SYSUTCDATETIME())
),
(
    'pt04',
    '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.',
    N'謝承恩',
    'pt04@example.com',
    8,
    'ACTIVE',
    DATEADD(DAY, -15, SYSUTCDATETIME())
);
/*原物料*/
INSERT INTO materials (code, [name], [unit], cost, safety_stock)
VALUES
('MAT001', N'阿薩姆紅茶葉', N'公斤', 320.00, 20.0000),
('MAT002', N'茉香綠茶葉', N'公斤', 280.00, 18.0000),
('MAT003', N'四季春烏龍茶葉', N'公斤', 360.00, 15.0000),
('MAT004', N'伯爵紅茶葉', N'公斤', 410.00, 12.0000),
('MAT005', N'奶精粉', N'袋', 950.00, 10.0000),
('MAT006', N'全脂鮮奶', N'瓶', 95.00, 30.0000),
('MAT007', N'煉乳', N'罐', 88.00, 24.0000),
('MAT008', N'果糖糖漿', N'桶', 720.00, 8.0000),
('MAT009', N'黑糖', N'公斤', 85.00, 25.0000),
('MAT010', N'黑糖珍珠', N'袋', 680.00, 12.0000),
('MAT011', N'椰果', N'桶', 520.00, 8.0000),
('MAT012', N'仙草凍', N'桶', 460.00, 6.0000),
('MAT013', N'布丁粉', N'袋', 780.00, 8.0000),
('MAT014', N'蘆薈果肉', N'罐', 350.00, 10.0000),
('MAT015', N'新鮮檸檬', N'公斤', 120.00, 15.0000),
('MAT016', N'百香果原汁', N'瓶', 260.00, 10.0000),
('MAT017', N'芒果果泥', N'瓶', 290.00, 10.0000),
('MAT018', N'七百毫升塑膠杯', N'箱', 1450.00, 5.0000),
('MAT019', N'飲料封口膜', N'卷', 420.00, 6.0000),
('MAT020', N'飲料粗吸管', N'箱', 380.00, 8.0000);
/*供應商*/
INSERT INTO suppliers ([name], [phone], [address], [email])
VALUES
(N'晨光食品原料有限公司', '02-2345-1001', N'台北市中正區忠孝東路一段10號', 'morning@example.com'),
(N'綠野農產企業社', '03-332-2002', N'桃園市桃園區中山路25號', 'greenfield@example.com'),
(N'大豐包材有限公司', '04-2233-3003', N'台中市北區三民路三段88號', 'dafeng@example.com'),
(N'好味茶葉行', '049-222-4004', N'南投縣南投市民族路120號', 'goodtea@example.com'),
(N'南方乳品股份有限公司', '06-225-5005', N'台南市中西區民生路二段35號', 'southmilk@example.com'),
(N'海港冷凍食品有限公司', '07-336-6006', N'高雄市前鎮區中山二路66號', 'harbor@example.com'),
(N'東岸物流企業社', '03-835-7007', N'花蓮縣花蓮市中華路150號', 'eastlogistics@example.com'),
(N'金順餐飲設備有限公司', '02-2988-8008', N'新北市三重區重新路四段99號', 'jinshun@example.com'),
(N'香甜糖業行', '05-222-9009', N'嘉義市西區文化路77號', 'sweet@example.com'),
(N'安心清潔用品有限公司', '08-732-1010', N'屏東縣屏東市自由路210號', 'clean@example.com'),
(N'北辰冷凍食品有限公司', '02-2788-1122', N'台北市南港區忠孝東路七段25號', 'beichen@example.com'),
(N'豐盛農產行', '03-555-2233', N'新竹縣竹北市光明六路88號', 'harvest@example.com'),
(N'四季鮮果有限公司', '04-2311-3344', N'台中市西屯區台灣大道三段120號', 'fruit@example.com'),
(N'晨曦食品原料有限公司', '02-2655-1212', N'台北市內湖區瑞光路168號', 'dawnfood@example.com'),
(N'綠田有機農產行', '03-368-2323', N'桃園市八德區介壽路二段95號', 'greenfarm@example.com'),
(N'大展餐飲包材有限公司', '04-2258-3434', N'台中市南屯區公益路二段188號', 'packaging@example.com'),
(N'清香茶葉企業社', '049-223-4545', N'南投縣名間鄉名松路一段65號', 'freshtea@example.com'),
(N'幸福乳品供應有限公司', '06-251-5656', N'台南市北區公園路520號', 'happydairy@example.com'),
(N'海味冷凍食品行', '07-521-6767', N'高雄市鼓山區臨海二路36號', 'seafood@example.com'),
(N'東方貨運企業有限公司', '03-857-7878', N'花蓮縣吉安鄉中央路三段110號', 'easttransport@example.com'),
(N'永順廚房設備有限公司', '02-2995-8989', N'新北市三重區重新路五段88號', 'kitchenware@example.com'),
(N'甜蜜糖品原料行', '05-534-9090', N'雲林縣斗六市中山路198號', 'sweetgoods@example.com'),
(N'潔淨清潔用品企業社', '08-755-2020', N'屏東縣屏東市建國路125號', 'cleaning@example.com'),
(N'日昇包裝材料行', '05-225-4455', N'嘉義市東區民族路76號', 'sunpack@example.com'),
(N'南星食品企業社', '06-298-5566', N'台南市安平區永華路二段56號', 'southstar@example.com'),
(N'高雄餐飲設備有限公司', '07-336-6677', N'高雄市苓雅區中正二路100號', 'khkitchen@example.com'),
(N'蘭陽有機農場', '03-932-7788', N'宜蘭縣宜蘭市中山路三段45號', 'lanyan@example.com'),
(N'東海乳品供應社', '04-2631-8899', N'台中市龍井區新興路22號', 'dairy@example.com'),
(N'金葉茶業有限公司', '049-264-9900', N'南投縣竹山鎮集山路二段138號', 'goldentea@example.com'),
(N'安心物流股份有限公司', '02-2299-1010', N'新北市五股區五權路60號', 'safelogistics@example.com');
/*採購單+採購明細*/
--原物料要先有!!
--供應商也要先有
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    DECLARE @first_purchase_order_id BIGINT;

    /* 先新增20張採購單，total暫時填0，新增明細後再自動計算 */
    INSERT INTO purchase_orders (supplier_id, [status], created_by, approved_by, [total], created_at, expected_delivery_date)
    VALUES
    (1, N'已完成', N'staff01', N'purchase_manager01', 0, '2026-08-10T09:15:00', '2026-08-15'),
    (2, N'已完成', N'staff02', N'purchase_manager01', 0, '2026-08-12T10:20:00', '2026-08-18'),
    (3, N'已完成', N'team_leader01', N'deputy_manager01', 0, '2026-08-15T11:30:00', '2026-08-22'),
    (4, N'部分到貨', N'staff03', N'purchase_manager01', 0, '2026-08-18T14:10:00', '2026-08-25'),
    (5, N'已完成', N'shift_leader01', N'store_manager01', 0, '2026-08-20T08:45:00', '2026-08-27'),
    (6, N'已下單', N'assistant_leader01', N'purchase_manager01', 0, '2026-08-23T13:25:00', '2026-08-30'),
    (7, N'已完成', N'staff04', N'deputy_manager01', 0, '2026-08-25T15:40:00', '2026-09-01'),
    (8, N'已核准', N'team_leader02', N'purchase_manager01', 0, '2026-08-28T09:50:00', '2026-09-06'),
    (9, N'已完成', N'staff05', N'store_manager01', 0, '2026-08-29T11:15:00', '2026-09-03'),
    (10, N'已下單', N'staff06', N'purchase_manager01', 0, '2026-08-30T16:20:00', '2026-09-08'),
    (11, N'部分到貨', N'shift_leader02', N'deputy_manager01', 0, '2026-09-01T08:30:00', '2026-09-09'),
    (12, N'已核准', N'assistant_leader02', N'purchase_manager01', 0, '2026-09-02T10:45:00', '2026-09-10'),
    (13, N'已取消', N'team_leader01', N'deputy_manager01', 0, '2026-09-02T14:30:00', '2026-09-12'),
    (14, N'已完成', N'staff01', N'purchase_manager01', 0, '2026-09-03T09:10:00', '2026-09-05'),
    (15, N'待審核', N'staff02', NULL, 0, '2026-09-03T13:40:00', '2026-09-11'),
    (16, N'已下單', N'team_leader02', N'purchase_manager01', 0, '2026-09-04T08:55:00', '2026-09-12'),
    (17, N'已核准', N'staff03', N'deputy_manager01', 0, '2026-09-04T11:35:00', '2026-09-13'),
    (18, N'待審核', N'staff04', NULL, 0, '2026-09-05T09:20:00', '2026-09-14'),
    (19, N'已取消', N'staff05', N'store_manager01', 0, '2026-09-05T15:10:00', '2026-09-15'),
    (20, N'待審核', N'staff06', NULL, 0, '2026-09-06T10:00:00', '2026-09-16');

    SET @first_purchase_order_id = CONVERT(BIGINT, SCOPE_IDENTITY()) - 19;

    /* 根據材料代碼取得material_id，並直接使用materials.cost作為採購單價 */
    INSERT INTO purchase_order_items (purchase_order_id, material_id, quantity, price)
    SELECT @first_purchase_order_id + source_data.order_offset, materials.id, source_data.quantity, materials.cost
    FROM
    (
        VALUES
        (0, 'MAT001', 10.000),
        (0, 'MAT005', 4.000),
        (1, 'MAT002', 8.000),
        (1, 'MAT008', 2.000),
        (2, 'MAT003', 6.000),
        (2, 'MAT009', 10.000),
        (3, 'MAT004', 5.000),
        (3, 'MAT007', 12.000),
        (4, 'MAT006', 30.000),
        (4, 'MAT007', 10.000),
        (5, 'MAT008', 3.000),
        (5, 'MAT009', 20.000),
        (6, 'MAT010', 5.000),
        (6, 'MAT009', 8.000),
        (7, 'MAT011', 4.000),
        (7, 'MAT014', 10.000),
        (8, 'MAT012', 4.000),
        (8, 'MAT013', 3.000),
        (9, 'MAT015', 15.000),
        (9, 'MAT016', 8.000),
        (10, 'MAT017', 6.000),
        (10, 'MAT015', 10.000),
        (11, 'MAT018', 4.000),
        (11, 'MAT019', 6.000),
        (12, 'MAT020', 5.000),
        (12, 'MAT018', 2.000),
        (13, 'MAT001', 7.000),
        (13, 'MAT002', 7.000),
        (14, 'MAT003', 8.000),
        (14, 'MAT004', 6.000),
        (15, 'MAT005', 3.000),
        (15, 'MAT006', 24.000),
        (16, 'MAT010', 6.000),
        (16, 'MAT011', 3.000),
        (17, 'MAT012', 5.000),
        (17, 'MAT013', 4.000),
        (18, 'MAT014', 8.000),
        (18, 'MAT016', 6.000),
        (19, 'MAT017', 8.000),
        (19, 'MAT020', 4.000)
    ) AS source_data (order_offset, material_code, quantity)
    INNER JOIN materials ON materials.code = source_data.material_code;

    /* 如果不是40筆，代表MAT001～MAT020有材料不存在 */
    IF (SELECT COUNT(*) FROM purchase_order_items WHERE purchase_order_id BETWEEN @first_purchase_order_id AND @first_purchase_order_id + 19) <> 40
    BEGIN
        THROW 50001, N'材料資料不完整，無法新增全部40筆採購明細。', 1;
    END;

    /* 自動將每張採購單的明細加總後寫入total */
    UPDATE purchase_orders
    SET purchase_orders.[total] = calculated_totals.calculated_total
    FROM purchase_orders
    INNER JOIN
    (
        SELECT purchase_order_id, SUM(quantity * price) AS calculated_total
        FROM purchase_order_items
        WHERE purchase_order_id BETWEEN @first_purchase_order_id AND @first_purchase_order_id + 19
        GROUP BY purchase_order_id
    ) AS calculated_totals ON purchase_orders.id = calculated_totals.purchase_order_id;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;

/*DELETE FROM inventory_logs;
DELETE FROM inventories;
DELETE FROM materials;*/
--關聯關係 會出問題
INSERT INTO materials
(code, name, unit, cost, safety_stock)
VALUES
('TEA-001', N'阿薩姆紅茶原葉', N'kg', 320.00, 20.0000),
('TEA-002', N'茉莉綠茶茶葉', N'kg', 360.00, 18.0000),
('TEA-003', N'四季春茶葉', N'kg', 380.00, 20.0000),
('MILK-001', N'低溫鮮乳', N'瓶', 85.00, 40.0000),
('MILK-002', N'燕麥奶', N'瓶', 105.00, 24.0000),
('TOP-001', N'特級波霸珍珠', N'kg', 95.00, 15.0000),
('TOP-002', N'椰果', N'kg', 110.00, 12.0000),
('TOP-003', N'仙草凍', N'kg', 80.00, 10.0000),
('SYR-001', N'果糖糖漿', N'L', 65.00, 30.0000),
('SYR-002', N'黑糖糖漿', N'L', 120.00, 15.0000),
('FRUIT-001', N'檸檬原汁', N'L', 150.00, 10.0000),
('FRUIT-002', N'百香果原汁', N'L', 180.00, 10.0000),
('PKG-001', N'700ml 飲料杯', N'個', 2.80, 500.0000),
('PKG-002', N'700ml 杯蓋', N'個', 1.20, 500.0000),
('PKG-003', N'粗吸管', N'支', 0.80, 1000.0000);


INSERT INTO inventories
(material_id, quantity, expiry_date, created_at)
VALUES
((SELECT id FROM materials WHERE code = 'TEA-001'), 12.0000, '2026-12-20', DATEADD(DAY, -30, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TEA-001'), 20.0000, '2027-02-15', DATEADD(DAY, -10, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TEA-002'), 5.0000, '2026-11-30', DATEADD(DAY, -25, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TEA-002'), 10.0000, '2027-01-20', DATEADD(DAY, -8, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TEA-003'), 6.0000, '2026-10-25', DATEADD(DAY, -20, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'MILK-001'), 15.0000, '2026-09-08', DATEADD(DAY, -3, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'MILK-001'), 20.0000, '2026-09-12', DATEADD(DAY, -2, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'MILK-001'), 20.0000, '2026-09-18', DATEADD(DAY, -1, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'MILK-002'), 8.0000, '2026-09-11', DATEADD(DAY, -5, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'MILK-002'), 12.0000, '2026-10-01', DATEADD(DAY, -1, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TOP-001'), 0.0000, '2026-09-07', DATEADD(DAY, -15, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TOP-001'), 10.0000, '2026-09-12', DATEADD(DAY, -4, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TOP-001'), 14.0000, '2026-09-20', DATEADD(DAY, -2, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TOP-002'), 4.0000, '2026-10-05', DATEADD(DAY, -12, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TOP-002'), 6.0000, '2026-11-10', DATEADD(DAY, -4, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TOP-003'), 3.0000, '2026-09-09', DATEADD(DAY, -2, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'SYR-001'), 30.0000, NULL, DATEADD(DAY, -20, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'SYR-001'), 20.0000, NULL, DATEADD(DAY, -5, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'SYR-002'), 18.0000, NULL, DATEADD(DAY, -8, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'FRUIT-001'), 2.0000, '2026-09-03', DATEADD(DAY, -10, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'FRUIT-001'), 7.0000, '2026-09-15', DATEADD(DAY, -2, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'FRUIT-002'), 4.0000, '2026-09-10', DATEADD(DAY, -3, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'PKG-001'), 700.0000, NULL, DATEADD(DAY, -20, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'PKG-001'), 500.0000, NULL, DATEADD(DAY, -3, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'PKG-002'), 400.0000, NULL, DATEADD(DAY, -6, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'PKG-003'), 300.0000, NULL, DATEADD(DAY, -5, SYSUTCDATETIME()));


INSERT INTO inventory_logs
(material_id, quantity, action, ref_id, created_at)
VALUES
((SELECT id FROM materials WHERE code = 'TEA-001'), 12.0000, 'STOCK_IN', NULL, DATEADD(DAY, -30, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TEA-001'), 20.0000, 'STOCK_IN', NULL, DATEADD(DAY, -10, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TEA-002'), 5.0000, 'STOCK_IN', NULL, DATEADD(DAY, -25, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TEA-002'), 10.0000, 'STOCK_IN', NULL, DATEADD(DAY, -8, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TEA-003'), 6.0000, 'STOCK_IN', NULL, DATEADD(DAY, -20, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'MILK-001'), 15.0000, 'STOCK_IN', NULL, DATEADD(DAY, -3, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'MILK-001'), 20.0000, 'STOCK_IN', NULL, DATEADD(DAY, -2, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'MILK-001'), 20.0000, 'STOCK_IN', NULL, DATEADD(DAY, -1, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'MILK-002'), 8.0000, 'STOCK_IN', NULL, DATEADD(DAY, -5, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'MILK-002'), 12.0000, 'STOCK_IN', NULL, DATEADD(DAY, -1, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TOP-001'), 8.0000, 'STOCK_IN', NULL, DATEADD(DAY, -15, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TOP-001'), -8.0000, 'SALE_DEDUCT', 10001, DATEADD(DAY, -8, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TOP-001'), 10.0000, 'STOCK_IN', NULL, DATEADD(DAY, -4, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TOP-001'), 14.0000, 'STOCK_IN', NULL, DATEADD(DAY, -2, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TOP-002'), 4.0000, 'STOCK_IN', NULL, DATEADD(DAY, -12, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'TOP-002'), 6.0000, 'STOCK_IN', NULL, DATEADD(DAY, -4, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'TOP-003'), 3.0000, 'STOCK_IN', NULL, DATEADD(DAY, -2, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'SYR-001'), 30.0000, 'STOCK_IN', NULL, DATEADD(DAY, -20, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'SYR-001'), 20.0000, 'STOCK_IN', NULL, DATEADD(DAY, -5, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'SYR-002'), 18.0000, 'STOCK_IN', NULL, DATEADD(DAY, -8, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'FRUIT-001'), 2.0000, 'STOCK_IN', NULL, DATEADD(DAY, -10, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'FRUIT-001'), 7.0000, 'STOCK_IN', NULL, DATEADD(DAY, -2, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'FRUIT-002'), 4.0000, 'STOCK_IN', NULL, DATEADD(DAY, -3, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'PKG-001'), 700.0000, 'STOCK_IN', NULL, DATEADD(DAY, -20, SYSUTCDATETIME())),
((SELECT id FROM materials WHERE code = 'PKG-001'), 500.0000, 'STOCK_IN', NULL, DATEADD(DAY, -3, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'PKG-002'), 400.0000, 'STOCK_IN', NULL, DATEADD(DAY, -6, SYSUTCDATETIME())),

((SELECT id FROM materials WHERE code = 'PKG-003'), 300.0000, 'STOCK_IN', NULL, DATEADD(DAY, -5, SYSUTCDATETIME()));


INSERT INTO product_categories
    (name, active)
VALUES
    (N'奶茶類', 1),
    (N'純茶類', 1),
    (N'果茶類', 1),
    (N'特調類', 1);
    INSERT INTO products
    (
        sku,
        name,
        category_id,
        selling_price,
        cost_price,
        unit,
        status
    )
VALUES
    (
        'DRINK-001',
        N'珍珠奶茶',
        1,
        65.00,
        0.00,
        N'杯',
        'ACTIVE'
    ),

    (
        'DRINK-002',
        N'四季春青茶',
        2,
        35.00,
        0.00,
        N'杯',
        'ACTIVE'
    ),

    (
        'DRINK-003',
        N'阿薩姆紅茶',
        2,
        30.00,
        0.00,
        N'杯',
        'ACTIVE'
    ),

    (
        'DRINK-004',
        N'百香綠茶',
        3,
        55.00,
        0.00,
        N'杯',
        'ACTIVE'
    ),

    (
        'DRINK-005',
        N'黑糖珍珠鮮奶',
        1,
        75.00,
        0.00,
        N'杯',
        'ACTIVE'
    ),

    (
        'DRINK-006',
        N'檸檬青茶',
        3,
        60.00,
        0.00,
        N'杯',
        'ACTIVE'
    ),

    (
        'DRINK-007',
        N'仙草凍奶茶',
        1,
        65.00,
        0.00,
        N'杯',
        'ACTIVE'
    ),

    (
        'DRINK-008',
        N'冬瓜檸檬',
        4,
        50.00,
        0.00,
        N'杯',
        'ACTIVE'
    );