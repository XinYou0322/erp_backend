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
SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- =========================================================
-- 固定供應商 ID = 1 ~ 30
-- 因為先前 purchase_orders 使用 supplier_id = 1 ~ 30
-- =========================================================
SET IDENTITY_INSERT suppliers ON;


-- =========================================================
-- 1. 茶葉供應商
-- =========================================================
INSERT INTO suppliers
(
    id,
    name,
    country_calling_code,
    phone,
    extension,
    address,
    email,
    status
)
VALUES
(
    1,
    N'台灣茗茶原料有限公司',
    '+886',
    '02-2788-1101',
    '101',
    N'台北市南港區忠孝東路七段120號',
    'tea01@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 2
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    2,
    N'清香茶業有限公司',
    '+886',
    '02-2678-2202',
    NULL,
    N'新北市鶯歌區中正一路88號',
    'tea02@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 3
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    3,
    N'四季春茶葉行',
    '+886',
    '04-2235-3303',
    '203',
    N'台中市北屯區崇德路二段156號',
    'tea03@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 4
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    4,
    N'伯爵茶品國際有限公司',
    '+886',
    '04-2326-4404',
    NULL,
    N'台中市西區公益路180號',
    'tea04@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 5. 奶精、粉類
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    5,
    N'香濃食品原料有限公司',
    '+886',
    '06-2535-5505',
    '305',
    N'台南市永康區中正南路420號',
    'powder05@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 6. 鮮奶
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    6,
    N'晨牧鮮乳有限公司',
    '+886',
    '05-2376-6606',
    NULL,
    N'嘉義市西區北港路310號',
    'milk06@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 7. 煉乳
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    7,
    N'甜香乳品有限公司',
    '+886',
    '07-3457-7707',
    '107',
    N'高雄市左營區博愛三路210號',
    'dairy07@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 8. 果糖
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    8,
    N'甘甜糖業有限公司',
    '+886',
    '07-5558-8808',
    NULL,
    N'高雄市鼓山區明誠三路520號',
    'sugar08@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 9. 黑糖
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    9,
    N'古早味黑糖食品行',
    '+886',
    '08-7329-9909',
    NULL,
    N'屏東縣屏東市自由路168號',
    'sugar09@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 10. 珍珠
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    10,
    N'珍好食品原料有限公司',
    '+886',
    '04-2561-1010',
    '210',
    N'台中市大雅區中清路三段350號',
    'pearl10@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 11. 椰果
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    11,
    N'椰香食品有限公司',
    '+886',
    '07-6211-1111',
    NULL,
    N'高雄市岡山區岡山路260號',
    'coconut11@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 12. 仙草
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    12,
    N'仙草之家食品有限公司',
    '+886',
    '03-5871-1212',
    NULL,
    N'新竹縣關西鎮中山東路105號',
    'grass12@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 13. 布丁粉
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    13,
    N'金品食品粉料有限公司',
    '+886',
    '06-2891-1313',
    '313',
    N'台南市東區崇德路525號',
    'pudding13@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 14. 蘆薈
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    14,
    N'綠田農產有限公司',
    '+886',
    '08-7751-1414',
    NULL,
    N'屏東縣里港鄉中山路118號',
    'aloe14@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 15. 檸檬
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    15,
    N'九如鮮果農產行',
    '+886',
    '08-7391-1515',
    NULL,
    N'屏東縣九如鄉九如路二段220號',
    'lemon15@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 16. 百香果
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    16,
    N'埔里百香果農產有限公司',
    '+886',
    '049-299-1616',
    '116',
    N'南投縣埔里鎮中山路三段186號',
    'passion16@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 17. 芒果
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    17,
    N'玉井鮮果實業有限公司',
    '+886',
    '06-5741-1717',
    NULL,
    N'台南市玉井區中正路155號',
    'mango17@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 18. 塑膠杯
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    18,
    N'永盛飲料容器有限公司',
    '+886',
    '07-7871-1818',
    '218',
    N'高雄市大寮區鳳林三路450號',
    'cup18@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 19. 封口膜
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    19,
    N'大華包裝材料有限公司',
    '+886',
    '07-3511-1919',
    NULL,
    N'高雄市楠梓區楠梓路380號',
    'package19@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 20. 吸管
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    20,
    N'環球餐飲耗材有限公司',
    '+886',
    '07-3332-2020',
    '320',
    N'高雄市前鎮區中山二路215號',
    'straw20@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 21
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    21,
    N'鼎盛茶飲原料有限公司',
    '+886',
    '04-2293-2121',
    NULL,
    N'台中市北區中清路一段330號',
    'drink21@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 22
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    22,
    N'好味食品原料行',
    '+886',
    '06-3582-2222',
    NULL,
    N'台南市中西區民生路二段198號',
    'food22@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 23
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    23,
    N'旺來餐飲原料有限公司',
    '+886',
    '07-3982-2323',
    '123',
    N'高雄市三民區建工路560號',
    'drink23@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 24
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    24,
    N'禾豐食品有限公司',
    '+886',
    '04-2472-2424',
    NULL,
    N'台中市南屯區五權西路二段410號',
    'food24@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 25
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    25,
    N'南台灣鮮果有限公司',
    '+886',
    '07-7022-2525',
    NULL,
    N'高雄市鳳山區光遠路275號',
    'fruit25@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 26
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    26,
    N'優鮮冷藏食品有限公司',
    '+886',
    '07-8152-2626',
    '226',
    N'高雄市前鎮區新生路240號',
    'fresh26@erp-supplier.com',
    'ACTIVE'
);


-- =========================================================
-- 27
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    27,
    N'東方茶品實業有限公司',
    '+886',
    '03-3552-2727',
    NULL,
    N'桃園市桃園區中正路680號',
    'tea27@erp-supplier.com',
    'PENDING'
);


-- =========================================================
-- 28
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    28,
    N'新味食品材料有限公司',
    '+886',
    '02-2282-2828',
    NULL,
    N'新北市蘆洲區長安街320號',
    'food28@erp-supplier.com',
    'PENDING'
);


-- =========================================================
-- 29
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    29,
    N'豐收農產有限公司',
    '+886',
    '05-5332-2929',
    NULL,
    N'雲林縣斗六市文化路368號',
    'farm29@erp-supplier.com',
    'PENDING'
);


-- =========================================================
-- 30
-- =========================================================
INSERT INTO suppliers
(
    id, name, country_calling_code, phone,
    extension, address, email, status
)
VALUES
(
    30,
    N'全聯餐飲物料有限公司',
    '+886',
    '07-5363-3030',
    '330',
    N'高雄市苓雅區中華四路185號',
    'material30@erp-supplier.com',
    'PENDING'
);


SET IDENTITY_INSERT suppliers OFF;


-- =========================================================
-- Supplier Notes
-- 部分供應商有備註，部分沒有
--
-- 假設：
-- User ID 1 ~ 5 已存在
-- supplier_note.id 為 IDENTITY，所以不用自行設定
-- =========================================================


-- Supplier 1：2 筆備註
INSERT INTO supplier_note
(
    supplier_id,
    remark,
    created_by_user_id,
    created_at,
    updated_at
)
VALUES
(
    1,
    N'茶葉品質穩定，過去合作交期皆正常。',
    1,
    '2026-08-10 09:20:00',
    '2026-08-10 09:20:00'
);

INSERT INTO supplier_note
(
    supplier_id,
    remark,
    created_by_user_id,
    created_at,
    updated_at
)
VALUES
(
    1,
    N'阿薩姆紅茶近期價格有小幅調整，下次採購前需重新確認報價。',
    2,
    '2026-09-01 10:30:00',
    '2026-09-01 10:30:00'
);


-- Supplier 3
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    3,
    N'四季春茶葉香氣表現良好，可列為長期合作供應商。',
    2,
    '2026-08-15 14:20:00',
    '2026-08-15 14:20:00'
);


-- Supplier 5：2 筆
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    5,
    N'奶精粉需存放於乾燥環境，收貨時注意外包裝是否受潮。',
    3,
    '2026-08-18 11:00:00',
    '2026-08-18 11:00:00'
);

INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    5,
    N'大量訂購可另外洽談價格。',
    1,
    '2026-09-02 15:10:00',
    '2026-09-02 15:10:00'
);


-- Supplier 6：鮮奶
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    6,
    N'鮮奶屬冷藏品，配送後須立即確認保存溫度及有效期限。',
    4,
    '2026-08-20 09:40:00',
    '2026-08-20 09:40:00'
);


-- Supplier 8
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    8,
    N'果糖糖漿最低訂購量為兩桶。',
    1,
    '2026-08-22 13:30:00',
    '2026-08-22 13:30:00'
);


-- Supplier 9
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    9,
    N'黑糖風味較濃，適合黑糖珍珠及黑糖鮮奶系列。',
    2,
    '2026-08-23 16:15:00',
    '2026-08-23 16:15:00'
);


-- Supplier 10：2 筆
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    10,
    N'珍珠每批到貨須確認製造日期。',
    3,
    '2026-08-25 10:20:00',
    '2026-08-25 10:20:00'
);

INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    10,
    N'目前供貨穩定，平均交期約三個工作天。',
    4,
    '2026-09-03 11:40:00',
    '2026-09-03 11:40:00'
);


-- Supplier 12
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    12,
    N'仙草凍夏季需求較高，建議提前備貨。',
    2,
    '2026-08-27 14:00:00',
    '2026-08-27 14:00:00'
);


-- Supplier 14
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    14,
    N'蘆薈果肉開封後保存期限較短，採購量不宜過高。',
    5,
    '2026-08-29 09:30:00',
    '2026-08-29 09:30:00'
);


-- Supplier 15：2 筆
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    15,
    N'檸檬為產地直送，價格可能依季節波動。',
    1,
    '2026-08-30 10:10:00',
    '2026-08-30 10:10:00'
);

INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    15,
    N'最近一批檸檬品質良好，果汁量充足。',
    3,
    '2026-09-05 13:20:00',
    '2026-09-05 13:20:00'
);


-- Supplier 16
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    16,
    N'百香果原汁需冷藏保存，收貨時注意瓶身完整。',
    2,
    '2026-09-01 14:30:00',
    '2026-09-01 14:30:00'
);


-- Supplier 17
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    17,
    N'芒果果泥於芒果產季時價格較優惠。',
    4,
    '2026-09-02 09:50:00',
    '2026-09-02 09:50:00'
);


-- Supplier 18：2 筆
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    18,
    N'塑膠杯大量採購時需確認倉庫剩餘空間。',
    1,
    '2026-09-03 15:00:00',
    '2026-09-03 15:00:00'
);

INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    18,
    N'七百毫升杯目前交期約五個工作天。',
    5,
    '2026-09-06 11:30:00',
    '2026-09-06 11:30:00'
);


-- Supplier 19
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    19,
    N'封口膜規格需與目前門市封膜機尺寸一致。',
    3,
    '2026-09-04 13:40:00',
    '2026-09-04 13:40:00'
);


-- Supplier 20
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    20,
    N'粗吸管主要供珍珠類飲品使用，庫存低於安全量時應優先補貨。',
    2,
    '2026-09-05 10:00:00',
    '2026-09-05 10:00:00'
);


-- Supplier 21
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    21,
    N'可同時提供多種茶葉與飲料原料，適合作為備用供應商。',
    4,
    '2026-09-07 16:20:00',
    '2026-09-07 16:20:00'
);


-- Supplier 23
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    23,
    N'合作回覆速度快，臨時追加訂單可先電話確認庫存。',
    1,
    '2026-09-08 14:10:00',
    '2026-09-08 14:10:00'
);


-- Supplier 25
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    25,
    N'主要供應南部水果，可依季節提供不同品項。',
    3,
    '2026-09-09 09:20:00',
    '2026-09-09 09:20:00'
);


-- Supplier 26
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    26,
    N'冷藏配送需提前一天確認到貨時段。',
    5,
    '2026-09-10 15:30:00',
    '2026-09-10 15:30:00'
);


-- Supplier 27：目前審核中
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    27,
    N'新供應商，報價資料已收到，目前等待供應商資格審核。',
    2,
    '2026-09-11 10:30:00',
    '2026-09-11 10:30:00'
);


-- Supplier 28：目前審核中
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    28,
    N'樣品測試中，確認品質後再決定是否正式合作。',
    4,
    '2026-09-12 14:40:00',
    '2026-09-12 14:40:00'
);


-- Supplier 29
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    29,
    N'等待農產品來源及相關資料確認。',
    1,
    '2026-09-13 09:50:00',
    '2026-09-13 09:50:00'
);


-- Supplier 30
INSERT INTO supplier_note
(
    supplier_id, remark, created_by_user_id,
    created_at, updated_at
)
VALUES
(
    30,
    N'新合作廠商，目前正在確認商品目錄及合作條件。',
    3,
    '2026-09-14 11:20:00',
    '2026-09-14 11:20:00'
);


COMMIT TRANSACTION;
/*採購單+採購明細*/
--原物料要先有!!
--供應商也要先有








SET XACT_ABORT ON;
BEGIN TRANSACTION;

DECLARE @poId BIGINT;

-- 如果你的 Enum 不是 PENDING，只需要修改這裡
DECLARE @status VARCHAR(50) = 'PENDING_APPROVAL';


-- =========================================================
-- 採購單 01
-- total = 320*5 + 95*24 + 85*10 = 4730
-- =========================================================
INSERT INTO purchase_orders
(
    order_number,
    supplier_id,
    status,
    created_by_user_id,
    approved_by_user_id,
    received_by_user_id,
    total,
    created_at,
    updated_at,
    expected_delivery_date,
    received_at,
    receipt_url,
    decision_remark
)
VALUES
(
    'PO-202609-001',
    1,
    @status,
    1,
    3,
    NULL,
    4730.00,
    '2026-09-01 09:00:00',
    '2026-09-01 09:00:00',
    '2026-09-06',
    NULL,
    NULL,
    N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(
    purchase_order_id,
    material_id,
    quantity,
    price
)
VALUES
(@poId, 1, 5.0000, 320.00),   -- 阿薩姆紅茶葉
(@poId, 6, 24.0000, 95.00),   -- 全脂鮮奶
(@poId, 9, 10.0000, 85.00);   -- 黑糖


-- =========================================================
-- 採購單 02
-- total = 3136
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-002', 2, @status,
    2, 4,
    NULL,
    3136.00,
    '2026-09-01 10:00:00',
    '2026-09-01 10:00:00',
    '2026-09-06',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 2, 4.0000, 280.00),   -- 茉香綠茶葉
(@poId, 7, 12.0000, 88.00),   -- 煉乳
(@poId, 15, 8.0000, 120.00);  -- 新鮮檸檬


-- =========================================================
-- 採購單 03
-- total = 5500
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-003', 3, @status,
    3, 5,
    NULL,
    5500.00,
    '2026-09-02 09:00:00',
    '2026-09-02 09:00:00',
    '2026-09-07',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 3, 6.0000, 360.00),   -- 四季春烏龍茶葉
(@poId, 10, 3.0000, 680.00),  -- 黑糖珍珠
(@poId, 16, 5.0000, 260.00);  -- 百香果原汁


-- =========================================================
-- 採購單 04
-- total = 5610
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-004', 4, @status,
    4, 2,
    NULL,
    5610.00,
    '2026-09-02 10:00:00',
    '2026-09-02 10:00:00',
    '2026-09-07',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 4, 5.0000, 410.00),   -- 伯爵紅茶葉
(@poId, 11, 2.0000, 520.00),  -- 椰果
(@poId, 19, 6.0000, 420.00);  -- 飲料封口膜


-- =========================================================
-- 採購單 05
-- total = 6590
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-005', 5, @status,
    1, 3,
    NULL,
    6590.00,
    '2026-09-03 09:00:00',
    '2026-09-03 09:00:00',
    '2026-09-08',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 5, 3.0000, 950.00),   -- 奶精粉
(@poId, 12, 4.0000, 460.00),  -- 仙草凍
(@poId, 20, 5.0000, 380.00);  -- 飲料粗吸管


-- =========================================================
-- 採購單 06
-- total = 7090
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-006', 6, @status,
    2, 4,
    NULL,
    7090.00,
    '2026-09-03 10:00:00',
    '2026-09-03 10:00:00',
    '2026-09-08',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 6, 30.0000, 95.00),   -- 全脂鮮奶
(@poId, 14, 10.0000, 250.00), -- 蘆薈果肉
(@poId, 17, 6.0000, 290.00);  -- 芒果果泥


-- =========================================================
-- 採購單 07
-- total = 5192
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-007', 7, @status,
    3, 5,
    NULL,
    5192.00,
    '2026-09-04 09:00:00',
    '2026-09-04 09:00:00',
    '2026-09-09',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 7, 24.0000, 88.00),   -- 煉乳
(@poId, 15, 15.0000, 120.00), -- 新鮮檸檬
(@poId, 1, 4.0000, 320.00);   -- 阿薩姆紅茶葉


-- =========================================================
-- 採購單 08
-- total = 4590
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-008', 8, @status,
    4, 2,
    NULL,
    4590.00,
    '2026-09-04 10:00:00',
    '2026-09-04 10:00:00',
    '2026-09-09',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 8, 2.0000, 720.00),    -- 果糖糖漿
(@poId, 18, 1.0000, 1450.00),  -- 七百毫升塑膠杯
(@poId, 9, 20.0000, 85.00);    -- 黑糖


-- =========================================================
-- 採購單 09
-- total = 6245
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-009', 9, @status,
    1, 3,
    NULL,
    6245.00,
    '2026-09-05 09:00:00',
    '2026-09-05 09:00:00',
    '2026-09-10',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 9, 25.0000, 85.00),   -- 黑糖
(@poId, 10, 4.0000, 680.00),  -- 黑糖珍珠
(@poId, 2, 5.0000, 280.00);   -- 茉香綠茶葉


-- =========================================================
-- 採購單 10
-- total = 7080
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-010', 10, @status,
    2, 4,
    NULL,
    7080.00,
    '2026-09-05 10:00:00',
    '2026-09-05 10:00:00',
    '2026-09-10',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 10, 6.0000, 680.00),  -- 黑糖珍珠
(@poId, 11, 3.0000, 520.00),  -- 椰果
(@poId, 3, 4.0000, 360.00);   -- 四季春烏龍茶葉


-- =========================================================
-- 採購單 11
-- total = 5510
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-011', 11, @status,
    3, 5,
    NULL,
    5510.00,
    '2026-09-06 09:00:00',
    '2026-09-06 09:00:00',
    '2026-09-11',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 11, 4.0000, 520.00), -- 椰果
(@poId, 12, 3.0000, 460.00), -- 仙草凍
(@poId, 4, 5.0000, 410.00);  -- 伯爵紅茶葉


-- =========================================================
-- 採購單 12
-- total = 5760
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-012', 12, @status,
    4, 2,
    NULL,
    5760.00,
    '2026-09-06 10:00:00',
    '2026-09-06 10:00:00',
    '2026-09-11',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 12, 5.0000, 460.00), -- 仙草凍
(@poId, 13, 2.0000, 780.00), -- 布丁粉
(@poId, 5, 2.0000, 950.00);  -- 奶精粉


-- =========================================================
-- 採購單 13
-- total = 7240
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-013', 13, @status,
    1, 3,
    NULL,
    7240.00,
    '2026-09-07 09:00:00',
    '2026-09-07 09:00:00',
    '2026-09-12',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 13, 3.0000, 780.00), -- 布丁粉
(@poId, 14, 12.0000, 250.00),-- 蘆薈果肉
(@poId, 6, 20.0000, 95.00);  -- 全脂鮮奶


-- =========================================================
-- 採購單 14
-- total = 6534
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-014', 14, @status,
    2, 4,
    NULL,
    6534.00,
    '2026-09-07 10:00:00',
    '2026-09-07 10:00:00',
    '2026-09-12',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 14, 15.0000, 250.00), -- 蘆薈果肉
(@poId, 15, 10.0000, 120.00), -- 新鮮檸檬
(@poId, 7, 18.0000, 88.00);   -- 煉乳


-- =========================================================
-- 採購單 15
-- total = 5920
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-015', 15, @status,
    3, 5,
    NULL,
    5920.00,
    '2026-09-08 09:00:00',
    '2026-09-08 09:00:00',
    '2026-09-13',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 15, 20.0000, 120.00), -- 新鮮檸檬
(@poId, 16, 8.0000, 260.00),  -- 百香果原汁
(@poId, 8, 2.0000, 720.00);   -- 果糖糖漿


-- =========================================================
-- 採購單 16
-- total = 6600
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-016', 16, @status,
    4, 2,
    NULL,
    6600.00,
    '2026-09-08 10:00:00',
    '2026-09-08 10:00:00',
    '2026-09-13',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 16, 10.0000, 260.00), -- 百香果原汁
(@poId, 17, 5.0000, 290.00),  -- 芒果果泥
(@poId, 9, 30.0000, 85.00);   -- 黑糖


-- =========================================================
-- 採購單 17
-- total = 6200
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-017', 17, @status,
    1, 3,
    NULL,
    6200.00,
    '2026-09-09 09:00:00',
    '2026-09-09 09:00:00',
    '2026-09-14',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 17, 7.0000, 290.00),   -- 芒果果泥
(@poId, 18, 1.0000, 1450.00),  -- 七百毫升塑膠杯
(@poId, 10, 4.0000, 680.00);   -- 黑糖珍珠


-- =========================================================
-- 採購單 18
-- total = 7820
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-018', 18, @status,
    2, 4,
    NULL,
    7820.00,
    '2026-09-09 10:00:00',
    '2026-09-09 10:00:00',
    '2026-09-14',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 18, 2.0000, 1450.00), -- 七百毫升塑膠杯
(@poId, 19, 8.0000, 420.00),  -- 飲料封口膜
(@poId, 11, 3.0000, 520.00);  -- 椰果


-- =========================================================
-- 採購單 19
-- total = 8320
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-019', 19, @status,
    3, 5,
    NULL,
    8320.00,
    '2026-09-10 09:00:00',
    '2026-09-10 09:00:00',
    '2026-09-15',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 19, 10.0000, 420.00), -- 飲料封口膜
(@poId, 20, 6.0000, 380.00),  -- 飲料粗吸管
(@poId, 12, 4.0000, 460.00);  -- 仙草凍


-- =========================================================
-- 採購單 20
-- total = 6200
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-020', 20, @status,
    4, 2,
    NULL,
    6200.00,
    '2026-09-10 10:00:00',
    '2026-09-10 10:00:00',
    '2026-09-15',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 20, 8.0000, 380.00), -- 飲料粗吸管
(@poId, 1, 5.0000, 320.00),  -- 阿薩姆紅茶葉
(@poId, 13, 2.0000, 780.00); -- 布丁粉


-- =========================================================
-- 採購單 21
-- total = 7180
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-021', 21, @status,
    1, 3,
    NULL,
    7180.00,
    '2026-09-11 09:00:00',
    '2026-09-11 09:00:00',
    '2026-09-16',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 1, 8.0000, 320.00),  -- 阿薩姆紅茶葉
(@poId, 2, 6.0000, 280.00),  -- 茉香綠茶葉
(@poId, 3, 5.0000, 360.00),  -- 四季春烏龍茶葉
(@poId, 6, 12.0000, 95.00);  -- 全脂鮮奶


-- =========================================================
-- 採購單 22
-- total = 7395
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-022', 22, @status,
    2, 4,
    NULL,
    7395.00,
    '2026-09-11 10:00:00',
    '2026-09-11 10:00:00',
    '2026-09-16',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 4, 6.0000, 410.00),  -- 伯爵紅茶葉
(@poId, 5, 2.0000, 950.00),  -- 奶精粉
(@poId, 7, 20.0000, 88.00),  -- 煉乳
(@poId, 9, 15.0000, 85.00);  -- 黑糖


-- =========================================================
-- 採購單 23
-- total = 8480
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-023', 23, @status,
    3, 5,
    NULL,
    8480.00,
    '2026-09-12 09:00:00',
    '2026-09-12 09:00:00',
    '2026-09-17',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 8, 3.0000, 720.00),  -- 果糖糖漿
(@poId, 10, 5.0000, 680.00), -- 黑糖珍珠
(@poId, 12, 2.0000, 460.00), -- 仙草凍
(@poId, 14, 8.0000, 250.00); -- 蘆薈果肉


-- =========================================================
-- 採購單 24
-- total = 6820
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-024', 24, @status,
    4, 2,
    NULL,
    6820.00,
    '2026-09-12 10:00:00',
    '2026-09-12 10:00:00',
    '2026-09-17',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 11, 3.0000, 520.00), -- 椰果
(@poId, 13, 2.0000, 780.00), -- 布丁粉
(@poId, 15, 20.0000, 120.00),-- 新鮮檸檬
(@poId, 16, 5.0000, 260.00); -- 百香果原汁


-- =========================================================
-- 採購單 25
-- total = 8910
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-025', 25, @status,
    1, 3,
    NULL,
    8910.00,
    '2026-09-13 09:00:00',
    '2026-09-13 09:00:00',
    '2026-09-18',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 17, 6.0000, 290.00),   -- 芒果果泥
(@poId, 18, 1.0000, 1450.00),  -- 七百毫升塑膠杯
(@poId, 19, 10.0000, 420.00),  -- 飲料封口膜
(@poId, 20, 4.0000, 380.00);   -- 飲料粗吸管


-- =========================================================
-- 採購單 26
-- total = 10120
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-026', 26, @status,
    2, 4,
    NULL,
    10120.00,
    '2026-09-13 10:00:00',
    '2026-09-13 10:00:00',
    '2026-09-18',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 2, 10.0000, 280.00), -- 茉香綠茶葉
(@poId, 6, 24.0000, 95.00),  -- 全脂鮮奶
(@poId, 10, 3.0000, 680.00), -- 黑糖珍珠
(@poId, 14, 12.0000, 250.00);-- 蘆薈果肉


-- =========================================================
-- 採購單 27
-- total = 10600
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-027', 27, @status,
    3, 5,
    NULL,
    10600.00,
    '2026-09-14 09:00:00',
    '2026-09-14 09:00:00',
    '2026-09-19',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 3, 8.0000, 360.00),   -- 四季春烏龍茶葉
(@poId, 7, 30.0000, 88.00),   -- 煉乳
(@poId, 11, 4.0000, 520.00),  -- 椰果
(@poId, 15, 25.0000, 120.00); -- 新鮮檸檬


-- =========================================================
-- 採購單 28
-- total = 8170
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-028', 28, @status,
    4, 2,
    NULL,
    8170.00,
    '2026-09-14 10:00:00',
    '2026-09-14 10:00:00',
    '2026-09-19',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 4, 7.0000, 410.00),  -- 伯爵紅茶葉
(@poId, 8, 2.0000, 720.00),  -- 果糖糖漿
(@poId, 12, 5.0000, 460.00), -- 仙草凍
(@poId, 16, 6.0000, 260.00); -- 百香果原汁


-- =========================================================
-- 採購單 29
-- total = 10160
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-029', 29, @status,
    1, 3,
    NULL,
    10160.00,
    '2026-09-15 09:00:00',
    '2026-09-15 09:00:00',
    '2026-09-20',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 5, 4.0000, 950.00),  -- 奶精粉
(@poId, 9, 20.0000, 85.00),  -- 黑糖
(@poId, 13, 3.0000, 780.00), -- 布丁粉
(@poId, 17, 8.0000, 290.00); -- 芒果果泥


-- =========================================================
-- 採購單 30
-- total = 10310
-- =========================================================
INSERT INTO purchase_orders
(
    order_number, supplier_id, status,
    created_by_user_id, approved_by_user_id,
    received_by_user_id,
    total, created_at, updated_at,
    expected_delivery_date,
    received_at, receipt_url, decision_remark
)
VALUES
(
    'PO-202609-030', 30, @status,
    2, 4,
    NULL,
    10310.00,
    '2026-09-16 09:00:00',
    '2026-09-16 09:00:00',
    '2026-09-21',
    NULL, NULL, N'等待簽核'
);

SET @poId = SCOPE_IDENTITY();

INSERT INTO purchase_order_items
(purchase_order_id, material_id, quantity, price)
VALUES
(@poId, 1, 6.0000, 320.00),    -- 阿薩姆紅茶葉
(@poId, 18, 1.0000, 1450.00),  -- 七百毫升塑膠杯
(@poId, 19, 12.0000, 420.00),  -- 飲料封口膜
(@poId, 20, 5.0000, 380.00);   -- 飲料粗吸管


COMMIT TRANSACTION;

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



INSERT INTO workflows
(
    document_type,
    document_id,
    status,
    applicant_id,
    approver_id,
    created_at
)
VALUES
('LEAVE', 1001, 'PENDING', 2, 1, '2026-09-01T09:00:00'),

('ORDER', 2001, 'APPROVED', 3, 2, '2026-09-02T10:30:00'),

('LEAVE', 1002, 'REJECTED', 4, 2, '2026-09-03T14:00:00'),

('ORDER', 2002, 'PENDING', 5, 1, '2026-09-04T09:15:00'),

('ORDER', 2003, 'APPROVED', 3, 1, '2026-09-04T11:00:00');



INSERT INTO workflow_logs
(
    workflow_id,
    action,
    operator_id,
    remark,
    created_at
)
VALUES

(1, 'SUBMIT', 2, '身體不適，申請一天病假', '2026-09-01T09:00:00'),

(2, 'SUBMIT', 3, '申請採購珍珠 50kg、鮮奶 30箱', '2026-09-02T10:30:00'),
(2, 'APPROVE', 2, '採購內容確認無誤，核准申請', '2026-09-02T15:20:00'),

(3, 'SUBMIT', 4, '家中有事，申請請假一天', '2026-09-03T14:00:00'),
(3, 'REJECT', 2, '目前人力不足，無法核准此次請假', '2026-09-03T16:30:00'),

(4, 'SUBMIT', 5, '申請採購紅茶葉 20kg', '2026-09-04T09:15:00'),

(5, 'SUBMIT', 3, '申請辦公用品採購', '2026-09-04T11:00:00'),
(5, 'APPROVE', 1, '確認需求後核准採購', '2026-09-04T13:45:00');

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

    //打卡紀錄表
    CREATE TABLE clock_records (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,   -- 自動遞增，從 1 開始，每次加 1
    user_id VARCHAR(50) NOT NULL,          -- 員工編號
    clock_time DATETIME NOT NULL,          -- 打卡時間（由後端伺服器生成）
    clock_type VARCHAR(10) NOT NULL        -- 打卡類型：'CLOCK_IN' 或 'CLOCK_OUT'
);

-- 1. 建立行事曆主表
CREATE TABLE calendar_events (
    id VARCHAR(36) NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX) NULL,
    category VARCHAR(50) NOT NULL, -- procurement, production, meeting, etc.
    [date] DATE NOT NULL,          -- 行事曆日期 (yyyy-MM-dd)
    start_time TIME(0) NOT NULL,    -- 開始時間 (HH:mm:ss)
    end_time TIME(0) NOT NULL,      -- 結束時間 (HH:mm:ss)
    location NVARCHAR(255) NULL,
    organizer NVARCHAR(100) NULL,
    priority VARCHAR(20) NOT NULL,  -- high, medium, low
    [status] VARCHAR(20) NOT NULL,  -- pending, in_progress, completed, cancelled
    related_ref VARCHAR(100) NULL,  -- 關聯單據 (如 PO-2026-0301)
    reminder_minutes INT DEFAULT 15,
    created_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT PK_calendar_events PRIMARY KEY (id)
);

-- 2. 建立參與成員子表 (一對多關係)
CREATE TABLE event_attendees (
    event_id VARCHAR(36) NOT NULL,
    attendee_name NVARCHAR(100) NOT NULL,
    CONSTRAINT FK_event_attendees_calendar_events FOREIGN KEY (event_id) 
        REFERENCES calendar_events(id) ON DELETE CASCADE
);