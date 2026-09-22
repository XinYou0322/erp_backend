/*
===============================================================================
 飲料店 ERP 完整假資料（SQL Server）
===============================================================================

 依 erp_b.zip 目前的 @Entity 與 enum 產生。

 特點：
 1. 不會 DELETE 或清空既有資料。
 2. 主要資料以代碼、帳號、Email、單號判斷，可安全重複執行。
 3. 不使用 GO，可直接貼到 SSMS 或 DBeaver 執行。
 4. 所有 INSERT 都包在同一個 Transaction；任一段失敗會全部回滾。

 測試帳號密碼：Test1234!

 建議執行時機：
 - 先啟動 Spring Boot，讓 spring.jpa.hibernate.ddl-auto=create 建立資料表。
 - 再執行本檔。
===============================================================================
*/

SET NOCOUNT ON;
SET XACT_ABORT ON;

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
    THROW 50001, N'找不到 users 資料表，請先啟動 Spring Boot 建立資料表。', 1;

IF OBJECT_ID(N'dbo.purchase_orders', N'U') IS NULL
    THROW 50002, N'找不到 purchase_orders 資料表，請確認 Entity 已完成建表。', 1;

IF OBJECT_ID(N'dbo.sales_orders', N'U') IS NULL
    THROW 50003, N'找不到 sales_orders 資料表，請確認 Entity 已完成建表。', 1;

DECLARE @NowUtc datetime2 = SYSUTCDATETIME();
DECLARE @NowLocal datetime2 = SYSDATETIME();
DECLARE @Today date = CAST(GETDATE() AS date);
DECLARE @TestPasswordHash varchar(60) = '$2b$10$5XKTItquzJxaEHzpVN1a3.kijEfiZrPnuRo5xeuwRu/zQ1YCVSPo.';

BEGIN TRY
    BEGIN TRANSACTION;

    /* ======================================================================
       1. 角色
       ====================================================================== */
    DECLARE @RoleSeed TABLE
    (
        role_name nvarchar(50) PRIMARY KEY,
        description nvarchar(255)
    );

    INSERT INTO @RoleSeed (role_name, description)
    VALUES
        (N'店長', N'系統管理員 (Admin)'),
        (N'經理', N'營運經理 / 店長 (Manager)'),
        (N'正職', N'現場員工 / 收銀員 (Employee)'),
        (N'訪客', N'訪客 / 外部審計 (Guest)');

    INSERT INTO roles (name, description)
    SELECT rs.role_name, rs.description
    FROM @RoleSeed rs
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM roles r
        WHERE r.name = rs.role_name
    );

    UPDATE r
    SET r.description = rs.description
    FROM roles r
    INNER JOIN @RoleSeed rs ON rs.role_name = r.name
    WHERE r.description IS NULL OR r.description <> rs.description;

    /* ======================================================================
       2. 使用者
       所有測試帳號的明碼密碼都是：Test1234!
       ====================================================================== */
    DECLARE @UserSeed TABLE
    (
        username varchar(50) PRIMARY KEY,
        display_name nvarchar(50),
        email varchar(50),
        role_name nvarchar(50),
        user_status varchar(50),
        created_days_ago int
    );

    INSERT INTO @UserSeed
        (username, display_name, email, role_name, user_status, created_days_ago)
    VALUES
        ('store_manager01',    N'陳志明', 'store.manager01@example.com',    N'店長',   'ACTIVE',   180),
        ('deputy_manager01',   N'林雅雯', 'deputy.manager01@example.com',   N'經理',   'ACTIVE',   170),
        ('purchase_manager01', N'王建國', 'purchase.manager01@example.com', N'經理',   'ACTIVE',   160),
        ('sales_manager01',    N'張淑芬', 'sales.manager01@example.com',    N'經理',   'ACTIVE',   150),
        ('team_leader01',      N'黃俊傑', 'team.leader01@example.com',      N'經理',   'ACTIVE',   140),
        ('assistant_leader01', N'吳柏翰', 'assistant.leader01@example.com', N'經理',   'ACTIVE',   130),
        ('shift_leader01',     N'劉冠廷', 'shift.leader01@example.com',     N'正職',   'ACTIVE',   120),
        ('staff01',            N'王小明', 'staff01@example.com',            N'正職',   'ACTIVE',   110),
        ('staff02',            N'李佳穎', 'staff02@example.com',            N'正職',   'ACTIVE',   100),
        ('staff03',            N'張志豪', 'staff03@example.com',            N'正職',   'ACTIVE',    90),
        ('staff04',            N'黃雅婷', 'staff04@example.com',            N'正職',   'ACTIVE',    80),
        ('staff05',            N'陳柏宇', 'staff05@example.com',            N'正職',   'ACTIVE',    70),
        ('staff06',            N'林佩芸', 'staff06@example.com',            N'正職',   'INACTIVE',  60),
        ('pt01',               N'周子晴', 'pt01@example.com',               N'正職',   'ACTIVE',    50),
        ('pt02',               N'許家豪', 'pt02@example.com',               N'正職',   'ACTIVE',    40),
        ('pt03',               N'郭欣怡', 'pt03@example.com',               N'正職',   'LOCKED',    30),
        ('guest_auditor01',    N'外部審計員', 'guest.auditor01@example.com', N'訪客',   'ACTIVE',    20);

    INSERT INTO users
        (username, password, name, email, role_id, avatar, status, created_at)
    SELECT
        us.username,
        @TestPasswordHash,
        us.display_name,
        us.email,
        r.id,
        NULL,
        us.user_status,
        DATEADD(DAY, -us.created_days_ago, @NowUtc)
    FROM @UserSeed us
    INNER JOIN roles r
        ON r.name = us.role_name
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM users u
        WHERE u.username = us.username
           OR u.email = us.email
    );

    -- 重跑腳本時，也把既有測試帳號的角色更新到四種系統角色。
    UPDATE u
    SET u.role_id = r.id
    FROM users u
    INNER JOIN @UserSeed us ON us.username = u.username
    INNER JOIN roles r ON r.name = us.role_name
    WHERE u.role_id <> r.id;

    /* ======================================================================
       3. 商品分類
       ====================================================================== */
    DECLARE @CategorySeed TABLE
    (
        category_name nvarchar(50) PRIMARY KEY,
        active bit
    );

    INSERT INTO @CategorySeed (category_name, active)
    VALUES
        (N'原茶類', 1),
        (N'奶茶類', 1),
        (N'鮮果茶類', 1),
        (N'特調類', 1),
        (N'咖啡類', 1),
        (N'下架商品', 0);

    INSERT INTO product_categories (name, active)
    SELECT cs.category_name, cs.active
    FROM @CategorySeed cs
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM product_categories pc
        WHERE pc.name = cs.category_name
    );

    /* ======================================================================
       4. 原物料

       cost_mode：DIRECT / CONVERSION
       status：ACTIVE / INACTIVE
       ====================================================================== */
    DECLARE @MaterialSeed TABLE
    (
        code varchar(50) PRIMARY KEY,
        material_name nvarchar(100),
        unit nvarchar(50),
        cost_mode varchar(50),
        purchase_unit nvarchar(50) NULL,
        material_status varchar(20),
        conversion_quantity decimal(18,4) NULL,
        purchase_cost decimal(18,4) NULL,
        cost decimal(18,4),
        safety_stock decimal(18,4) NULL
    );

    INSERT INTO @MaterialSeed
        (code, material_name, unit, cost_mode, purchase_unit, material_status,
         conversion_quantity, purchase_cost, cost, safety_stock)
    VALUES
        ('MAT001', N'阿薩姆紅茶葉', N'g',  'CONVERSION', N'包', 'ACTIVE', 2500.0000,  800.0000, 0.3200, 5000.0000),
        ('MAT002', N'茉香綠茶葉',   N'g',  'CONVERSION', N'包', 'ACTIVE', 2500.0000,  775.0000, 0.3100, 5000.0000),
        ('MAT003', N'四季春茶葉',   N'g',  'CONVERSION', N'包', 'ACTIVE', 2500.0000, 1012.5000, 0.4050, 4000.0000),
        ('MAT004', N'伯爵紅茶葉',   N'g',  'CONVERSION', N'包', 'ACTIVE', 2500.0000, 1500.0000, 0.6000, 3000.0000),
        ('MAT005', N'全脂鮮奶',     N'ml', 'CONVERSION', N'瓶', 'ACTIVE', 1858.0000,   92.9000, 0.0500, 8000.0000),
        ('MAT006', N'奶精粉',       N'g',  'CONVERSION', N'包', 'ACTIVE', 1000.0000,  235.0000, 0.2350, 3000.0000),
        ('MAT007', N'果糖糖漿',     N'ml', 'CONVERSION', N'桶', 'ACTIVE', 5000.0000,  520.0000, 0.1040, 8000.0000),
        ('MAT008', N'黑糖珍珠',     N'g',  'CONVERSION', N'包', 'ACTIVE', 3000.0000,  150.0000, 0.0500, 6000.0000),
        ('MAT009', N'椰果',         N'g',  'CONVERSION', N'桶', 'ACTIVE', 3000.0000,  240.0000, 0.0800, 3000.0000),
        ('MAT010', N'仙草凍',       N'g',  'CONVERSION', N'桶', 'ACTIVE', 3000.0000,  195.0000, 0.0650, 3000.0000),
        ('MAT011', N'檸檬原汁',     N'ml', 'CONVERSION', N'桶', 'ACTIVE', 5000.0000,  830.0000, 0.1660, 5000.0000),
        ('MAT012', N'百香果原汁',   N'ml', 'CONVERSION', N'桶', 'ACTIVE', 5000.0000, 1100.0000, 0.2200, 4000.0000),
        ('MAT013', N'芒果果泥',     N'ml', 'CONVERSION', N'桶', 'ACTIVE', 5000.0000, 1000.0000, 0.2000, 4000.0000),
        ('MAT014', N'咖啡豆',       N'g',  'CONVERSION', N'包', 'ACTIVE', 1000.0000,  750.0000, 0.7500, 2000.0000),
        ('MAT015', N'巧克力醬',     N'ml', 'CONVERSION', N'桶', 'ACTIVE', 5000.0000,  900.0000, 0.1800, 3000.0000),
        ('MAT016', N'700ml飲料杯',  N'個', 'CONVERSION', N'箱', 'ACTIVE', 1000.0000, 1800.0000, 1.8000,  500.0000),
        ('MAT017', N'飲料封口膜',   N'張', 'CONVERSION', N'卷', 'ACTIVE', 3000.0000,  900.0000, 0.3000, 1000.0000),
        ('MAT018', N'粗吸管',       N'支', 'CONVERSION', N'箱', 'ACTIVE', 2000.0000, 1000.0000, 0.5000,  800.0000),
        ('MAT019', N'製冰用冰塊',   N'g',  'DIRECT',     NULL, 'ACTIVE',       NULL,       NULL, 0.0020,10000.0000),
        ('MAT020', N'過濾水',       N'ml', 'DIRECT',     NULL, 'ACTIVE',       NULL,       NULL, 0.0010,20000.0000),
        ('MAT021', N'舊版奶精粉',   N'g',  'CONVERSION', N'包', 'INACTIVE',1000.0000,  160.0000, 0.1600, 1000.0000);

    INSERT INTO materials
        (code, name, unit, cost_mode, purchase_unit, status,
         conversion_quantity, purchase_cost, cost, safety_stock)
    SELECT
        ms.code,
        ms.material_name,
        ms.unit,
        ms.cost_mode,
        ms.purchase_unit,
        ms.material_status,
        ms.conversion_quantity,
        ms.purchase_cost,
        ms.cost,
        ms.safety_stock
    FROM @MaterialSeed ms
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM materials m
        WHERE m.code = ms.code
    );

    /* ======================================================================
       5. 商品
       ====================================================================== */
    DECLARE @ProductSeed TABLE
    (
        sku varchar(50) PRIMARY KEY,
        product_name nvarchar(100),
        category_name nvarchar(50),
        selling_price decimal(18,2),
        cost_price decimal(18,2),
        unit nvarchar(50),
        product_status varchar(50)
    );

    INSERT INTO @ProductSeed
        (sku, product_name, category_name, selling_price, cost_price, unit, product_status)
    VALUES
        ('P001', N'阿薩姆紅茶',     N'原茶類',   35.00, 10.75, N'杯', 'ACTIVE'),
        ('P002', N'茉香綠茶',       N'原茶類',   35.00, 10.60, N'杯', 'ACTIVE'),
        ('P003', N'四季春青茶',     N'原茶類',   40.00, 12.03, N'杯', 'ACTIVE'),
        ('P004', N'伯爵紅茶',       N'原茶類',   45.00, 14.95, N'杯', 'ACTIVE'),
        ('P005', N'經典奶茶',       N'奶茶類',   50.00, 17.77, N'杯', 'ACTIVE'),
        ('P006', N'伯爵奶茶',       N'奶茶類',   55.00, 21.97, N'杯', 'ACTIVE'),
        ('P007', N'紅茶鮮奶',       N'奶茶類',   65.00, 22.48, N'杯', 'ACTIVE'),
        ('P008', N'珍珠奶茶',       N'奶茶類',   60.00, 21.75, N'杯', 'ACTIVE'),
        ('P009', N'黑糖珍珠鮮奶',   N'奶茶類',   75.00, 24.98, N'杯', 'ACTIVE'),
        ('P010', N'檸檬紅茶',       N'鮮果茶類', 55.00, 17.34, N'杯', 'ACTIVE'),
        ('P011', N'翡翠檸檬',       N'鮮果茶類', 60.00, 18.02, N'杯', 'ACTIVE'),
        ('P012', N'百香綠茶',       N'鮮果茶類', 60.00, 21.02, N'杯', 'ACTIVE'),
        ('P013', N'芒果青茶',       N'鮮果茶類', 65.00, 23.44, N'杯', 'ACTIVE'),
        ('P014', N'椰果綠茶',       N'特調類',   50.00, 16.93, N'杯', 'ACTIVE'),
        ('P015', N'仙草奶茶',       N'特調類',   60.00, 23.68, N'杯', 'ACTIVE'),
        ('P016', N'美式咖啡',       N'咖啡類',   55.00, 16.69, N'杯', 'ACTIVE'),
        ('P017', N'拿鐵咖啡',       N'咖啡類',   75.00, 28.90, N'杯', 'ACTIVE'),
        ('P018', N'巧克力鮮奶',     N'特調類',   70.00, 25.00, N'杯', 'ACTIVE'),
        ('P019', N'百香雙Q綠茶',    N'特調類',   70.00, 26.96, N'杯', 'ACTIVE'),
        ('P020', N'黑糖珍珠奶茶',   N'奶茶類',   70.00, 21.05, N'杯', 'ACTIVE'),
        ('P021', N'舊版奶精紅茶',   N'下架商品', 45.00, 17.10, N'杯', 'INACTIVE');

    INSERT INTO products
        (sku, name, category_id, selling_price, cost_price, unit, status)
    SELECT
        ps.sku,
        ps.product_name,
        pc.id,
        ps.selling_price,
        ps.cost_price,
        ps.unit,
        ps.product_status
    FROM @ProductSeed ps
    INNER JOIN product_categories pc
        ON pc.name = ps.category_name
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM products p
        WHERE p.sku = ps.sku
    );

    /* ======================================================================
       6. BOM 配方
       ====================================================================== */
    DECLARE @BomSeed TABLE
    (
        sku varchar(50),
        material_code varchar(50),
        quantity decimal(18,4),
        PRIMARY KEY (sku, material_code)
    );

    INSERT INTO @BomSeed (sku, material_code, quantity)
    VALUES
        ('P001','MAT001',15.0000), ('P001','MAT007',25.0000), ('P001','MAT020',450.0000), ('P001','MAT019',150.0000),
        ('P002','MAT002',15.0000), ('P002','MAT007',25.0000), ('P002','MAT020',450.0000), ('P002','MAT019',150.0000),
        ('P003','MAT003',15.0000), ('P003','MAT007',25.0000), ('P003','MAT020',450.0000), ('P003','MAT019',150.0000),
        ('P004','MAT004',15.0000), ('P004','MAT007',25.0000), ('P004','MAT020',450.0000), ('P004','MAT019',150.0000),
        ('P005','MAT001',15.0000), ('P005','MAT006',30.0000), ('P005','MAT007',25.0000), ('P005','MAT020',420.0000), ('P005','MAT019',150.0000),
        ('P006','MAT004',15.0000), ('P006','MAT006',30.0000), ('P006','MAT007',25.0000), ('P006','MAT020',420.0000), ('P006','MAT019',150.0000),
        ('P007','MAT001',15.0000), ('P007','MAT005',250.0000),('P007','MAT007',20.0000), ('P007','MAT020',200.0000), ('P007','MAT019',150.0000),
        ('P008','MAT001',15.0000), ('P008','MAT006',30.0000), ('P008','MAT008',80.0000), ('P008','MAT007',25.0000), ('P008','MAT020',400.0000), ('P008','MAT019',150.0000),
        ('P009','MAT005',300.0000),('P009','MAT008',100.0000),('P009','MAT007',20.0000), ('P009','MAT019',150.0000),
        ('P010','MAT001',15.0000), ('P010','MAT011',40.0000), ('P010','MAT007',25.0000), ('P010','MAT020',400.0000), ('P010','MAT019',150.0000),
        ('P011','MAT002',15.0000), ('P011','MAT011',45.0000), ('P011','MAT007',25.0000), ('P011','MAT020',400.0000), ('P011','MAT019',150.0000),
        ('P012','MAT002',15.0000), ('P012','MAT012',50.0000), ('P012','MAT007',20.0000), ('P012','MAT020',390.0000), ('P012','MAT019',150.0000),
        ('P013','MAT003',15.0000), ('P013','MAT013',60.0000), ('P013','MAT007',20.0000), ('P013','MAT020',380.0000), ('P013','MAT019',150.0000),
        ('P014','MAT002',15.0000), ('P014','MAT009',80.0000), ('P014','MAT007',25.0000), ('P014','MAT020',380.0000), ('P014','MAT019',150.0000),
        ('P015','MAT001',15.0000), ('P015','MAT006',30.0000), ('P015','MAT010',100.0000),('P015','MAT007',20.0000), ('P015','MAT020',350.0000), ('P015','MAT019',150.0000),
        ('P016','MAT014',18.0000), ('P016','MAT020',350.0000),('P016','MAT019',120.0000),
        ('P017','MAT014',18.0000), ('P017','MAT005',250.0000),('P017','MAT020',100.0000),('P017','MAT019',100.0000),
        ('P018','MAT005',300.0000),('P018','MAT015',40.0000), ('P018','MAT019',100.0000),
        ('P019','MAT002',15.0000), ('P019','MAT012',50.0000), ('P019','MAT008',50.0000), ('P019','MAT009',50.0000), ('P019','MAT007',15.0000), ('P019','MAT020',350.0000), ('P019','MAT019',150.0000),
        ('P020','MAT001',15.0000), ('P020','MAT006',25.0000), ('P020','MAT008',80.0000), ('P020','MAT007',30.0000), ('P020','MAT020',350.0000), ('P020','MAT019',150.0000),
        ('P021','MAT001',15.0000), ('P021','MAT021',40.0000), ('P021','MAT007',25.0000), ('P021','MAT020',400.0000), ('P021','MAT019',150.0000);

    INSERT INTO bom (product_id, material_id, quantity)
    SELECT p.id, m.id, bs.quantity
    FROM @BomSeed bs
    INNER JOIN products p
        ON p.sku = bs.sku
    INNER JOIN materials m
        ON m.code = bs.material_code
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM bom b
        WHERE b.product_id = p.id
          AND b.material_id = m.id
    );

    -- 每杯飲料共同使用：飲料杯、封口膜、粗吸管。
    INSERT INTO bom (product_id, material_id, quantity)
    SELECT p.id, m.id, common_part.quantity
    FROM products p
    INNER JOIN @ProductSeed ps
        ON ps.sku = p.sku
    CROSS JOIN
    (
        VALUES
            ('MAT016', CAST(1.0000 AS decimal(18,4))),
            ('MAT017', CAST(1.0000 AS decimal(18,4))),
            ('MAT018', CAST(1.0000 AS decimal(18,4)))
    ) common_part(material_code, quantity)
    INNER JOIN materials m
        ON m.code = common_part.material_code
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM bom b
        WHERE b.product_id = p.id
          AND b.material_id = m.id
    );

    -- 商品成本一律以 BOM 用量 × 原物料單位成本加總，避免種子成本與配方不同步。
    UPDATE p
    SET p.cost_price = CAST(bom_cost.total_cost AS decimal(18,2))
    FROM products p
    INNER JOIN
    (
        SELECT b.product_id, SUM(b.quantity * m.cost) AS total_cost
        FROM bom b
        INNER JOIN materials m
            ON m.id = b.material_id
        GROUP BY b.product_id
    ) bom_cost
        ON bom_cost.product_id = p.id;

    /* ======================================================================
       7. 供應商（30 筆，可測試 10 / 30 / 50 分頁）
       ====================================================================== */
    DECLARE @SupplierSeed TABLE
    (
        supplier_no int PRIMARY KEY,
        supplier_name nvarchar(50),
        calling_code varchar(10),
        phone varchar(50),
        extension varchar(10) NULL,
        address nvarchar(200),
        email varchar(50),
        supplier_status varchar(30)
    );

    INSERT INTO @SupplierSeed
        (supplier_no, supplier_name, calling_code, phone, extension, address, email, supplier_status)
    VALUES
        ( 1,N'台灣茗茶原料有限公司','+886','02-2788-1101','101',N'台北市南港區忠孝東路七段120號','tea01@erp-supplier.com','ACTIVE'),
        ( 2,N'清香茶業有限公司',    '+886','02-2678-2202',NULL, N'新北市鶯歌區中正一路88號',      'tea02@erp-supplier.com','ACTIVE'),
        ( 3,N'四季春茶葉行',        '+886','04-2235-3303','203',N'台中市北屯區崇德路二段156號',   'tea03@erp-supplier.com','ACTIVE'),
        ( 4,N'伯爵茶品國際有限公司','+886','04-2326-4404',NULL, N'台中市西區公益路180號',         'tea04@erp-supplier.com','ACTIVE'),
        ( 5,N'香濃食品原料有限公司','+886','06-2535-5505','305',N'台南市永康區中正南路420號',    'powder05@erp-supplier.com','ACTIVE'),
        ( 6,N'每日鮮乳食品有限公司','+886','08-7654-6606',NULL, N'屏東縣屏東市牧場路20號',        'milk06@erp-supplier.com','ACTIVE'),
        ( 7,N'牧場直送乳品行',      '+886','05-3627-7707',NULL, N'嘉義縣太保市乳品路66號',        'milk07@erp-supplier.com','ACTIVE'),
        ( 8,N'甘蔗堂糖業股份有限公司','+886','07-3388-8808','108',N'高雄市前鎮區糖業路35號',      'sugar08@erp-supplier.com','ACTIVE'),
        ( 9,N'黑糖職人食品行',      '+886','06-2999-9909',NULL, N'台南市安平區健康三街52號',      'sugar09@erp-supplier.com','ACTIVE'),
        (10,N'珍珠王食品有限公司',  '+886','04-2510-1010','210',N'台中市豐原區食品街10號',       'pearl10@erp-supplier.com','ACTIVE'),
        (11,N'南洋椰果食品社',      '+886','07-3511-1111',NULL, N'高雄市楠梓區加工路77號',        'jelly11@erp-supplier.com','ACTIVE'),
        (12,N'古早味仙草企業社',    '+886','03-5222-1212',NULL, N'新竹市北區中山路260號',         'grass12@erp-supplier.com','ACTIVE'),
        (13,N'手作布丁原料公司',    '+886','04-2233-1313','313',N'台中市南屯區工業路90號',       'pudding13@erp-supplier.com','ACTIVE'),
        (14,N'綠源蘆薈農產行',      '+886','05-3744-1414',NULL, N'嘉義縣新港鄉農產路18號',        'aloe14@erp-supplier.com','ACTIVE'),
        (15,N'屏東鮮檸檬合作社',    '+886','08-7755-1515',NULL, N'屏東縣九如鄉果園路45號',        'lemon15@erp-supplier.com','ACTIVE'),
        (16,N'百香果農產有限公司',  '+886','049-276-1616',NULL,N'南投縣埔里鎮農園路39號',        'passion16@erp-supplier.com','ACTIVE'),
        (17,N'熱帶芒果產銷班',      '+886','08-8666-1717',NULL, N'屏東縣枋山鄉芒果路120號',       'mango17@erp-supplier.com','PENDING'),
        (18,N'永續包材有限公司',    '+886','03-4777-1818','118',N'桃園市觀音區工業五路8號',      'cup18@erp-supplier.com','ACTIVE'),
        (19,N'富利封口膜企業社',    '+886','02-2888-1919',NULL, N'台北市士林區承德路四段210號',   'film19@erp-supplier.com','ACTIVE'),
        (20,N'大口徑吸管有限公司',  '+886','07-3999-2020',NULL, N'高雄市三民區民族一路500號',     'straw20@erp-supplier.com','ACTIVE'),
        (21,N'北區綜合原料商行',    '+886','02-2211-2121','121',N'新北市新店區中央路77號',       'north21@erp-supplier.com','ACTIVE'),
        (22,N'中區食品配送公司',    '+886','04-2322-2222',NULL, N'台中市西屯區物流路188號',       'central22@erp-supplier.com','ACTIVE'),
        (23,N'南區冷鏈物流公司',    '+886','07-3333-2323','223',N'高雄市前鎮區冷鏈路35號',       'south23@erp-supplier.com','ACTIVE'),
        (24,N'東部農產合作社',      '+886','03-8444-2424',NULL, N'花蓮縣吉安鄉中央路二段88號',    'east24@erp-supplier.com','PENDING'),
        (25,N'海岸咖啡豆貿易公司',  '+886','02-2555-2525','125',N'台北市大同區迪化街一段66號',  'coffee25@erp-supplier.com','ACTIVE'),
        (26,N'可可風味食品公司',    '+886','06-2666-2626',NULL, N'台南市仁德區可可路26號',        'cocoa26@erp-supplier.com','ACTIVE'),
        (27,N'新創食品測試供應商',  '+886','02-2777-2727',NULL, N'台北市松山區測試路27號',        'trial27@erp-supplier.com','PENDING'),
        (28,N'延遲交貨測試供應商',  '+886','04-2888-2828',NULL, N'台中市大里區延遲路28號',        'delay28@erp-supplier.com','SUSPENDED'),
        (29,N'已停止合作供應商',    '+886','07-2999-2929',NULL, N'高雄市左營區舊合作路29號',      'inactive29@erp-supplier.com','INACTIVE'),
        (30,N'黑名單測試供應商',    '+886','03-3000-3030',NULL, N'桃園市中壢區風險路30號',        'black30@erp-supplier.com','BLACKLISTED');

    INSERT INTO suppliers
        (name, country_calling_code, phone, extension, address, email, status)
    SELECT
        ss.supplier_name,
        ss.calling_code,
        ss.phone,
        ss.extension,
        ss.address,
        ss.email,
        ss.supplier_status
    FROM @SupplierSeed ss
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM suppliers s
        WHERE s.email = ss.email
    );

    /* ======================================================================
       8. 供應商備註
       ====================================================================== */
    DECLARE @SupplierNoteSeed TABLE
    (
        supplier_email varchar(50),
        remark nvarchar(200),
        created_by_username varchar(50),
        created_days_ago int
    );

    INSERT INTO @SupplierNoteSeed
        (supplier_email, remark, created_by_username, created_days_ago)
    VALUES
        ('tea01@erp-supplier.com',     N'茶葉品質穩定，每月固定叫貨兩次。',             'purchase_manager01', 60),
        ('tea01@erp-supplier.com',     N'最近一次報價有效期限為月底。',                 'staff01',            20),
        ('tea03@erp-supplier.com',     N'四季春茶葉需提前七天預訂。',                   'purchase_manager01', 45),
        ('powder05@erp-supplier.com',  N'奶精粉到貨時需確認外箱是否受潮。',             'staff02',            35),
        ('milk06@erp-supplier.com',    N'鮮奶需要全程冷藏，收貨時檢查有效期限。',       'purchase_manager01', 30),
        ('sugar08@erp-supplier.com',   N'大量採購果糖可另談運費折扣。',                 'staff01',            28),
        ('pearl10@erp-supplier.com',   N'珍珠每批次需確認製造日期與保存方式。',         'staff03',            25),
        ('grass12@erp-supplier.com',   N'仙草凍夏季需求較高，建議提高安全庫存。',       'purchase_manager01', 22),
        ('lemon15@erp-supplier.com',   N'雨季檸檬價格波動較大，請先詢價。',             'staff02',            18),
        ('passion16@erp-supplier.com', N'百香果原汁開封後需冷藏。',                     'staff03',            16),
        ('mango17@erp-supplier.com',   N'新供應商，等待樣品與報價審核。',               'purchase_manager01', 14),
        ('cup18@erp-supplier.com',     N'杯材可印製品牌圖樣，最低訂購量為十箱。',       'staff01',            12),
        ('coffee25@erp-supplier.com',  N'咖啡豆烘焙日期須在出貨前七日內。',             'purchase_manager01', 10),
        ('trial27@erp-supplier.com',   N'測試合作中，尚未列入正式供應商。',             'staff02',             8),
        ('delay28@erp-supplier.com',   N'連續兩次延遲交貨，暫停新訂單。',               'purchase_manager01',  6),
        ('inactive29@erp-supplier.com',N'合約到期，已停止合作。',                       'staff03',             5),
        ('black30@erp-supplier.com',   N'曾發生嚴重品質異常，目前禁止交易。',           'store_manager01',     3);

    INSERT INTO supplier_note
        (supplier_id, remark, created_by_user_id, created_at, updated_at)
    SELECT
        s.id,
        sns.remark,
        u.id,
        DATEADD(DAY, -sns.created_days_ago, @NowLocal),
        DATEADD(DAY, -sns.created_days_ago, @NowLocal)
    FROM @SupplierNoteSeed sns
    INNER JOIN suppliers s
        ON s.email = sns.supplier_email
    INNER JOIN users u
        ON u.username = sns.created_by_username
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM supplier_note sn
        WHERE sn.supplier_id = s.id
          AND sn.remark = sns.remark
    );

    /* ======================================================================
       9. 庫存批次
       - 同一原物料可有多個批次。
       - 若某原物料已經有庫存批次，重跑時不會再塞入一組。
       ====================================================================== */
    DECLARE @InventorySeed TABLE
    (
        material_code varchar(50),
        batch_no int,
        quantity decimal(18,4),
        expiry_days_from_today int NULL,
        created_days_ago int,
        PRIMARY KEY (material_code, batch_no)
    );

    INSERT INTO @InventorySeed
        (material_code, batch_no, quantity, expiry_days_from_today, created_days_ago)
    VALUES
        ('MAT001',1, 7000.0000,120,20), ('MAT001',2,3500.0000, 60, 8),
        ('MAT002',1, 9000.0000,120,18), ('MAT002',2,3000.0000, 45, 6),
        ('MAT003',1, 6500.0000,100,17),
        ('MAT004',1, 4500.0000,110,16),
        ('MAT005',1, 6000.0000,  5, 2), ('MAT005',2,5000.0000, 12, 1),
        ('MAT006',1, 5000.0000,180,20),
        ('MAT007',1,14000.0000,240,25),
        ('MAT008',1, 4000.0000, 60, 8),
        ('MAT009',1, 4500.0000, 75,12),
        ('MAT010',1,2500.0000, 45,10),
        ('MAT011',1,3000.0000, 10, 3), ('MAT011',2,2500.0000, 25, 1),
        ('MAT012',1,5200.0000, 35, 6),
        ('MAT013',1,3600.0000, 30, 5),
        ('MAT014',1,3000.0000,150,15),
        ('MAT015',1,6000.0000,180,18),
        ('MAT016',1, 900.0000,NULL,14),
        ('MAT017',1,1500.0000,NULL,14),
        ('MAT018',1,1200.0000,NULL,14),
        ('MAT019',1,30000.0000,NULL, 2),
        ('MAT020',1,50000.0000,NULL, 1),
        ('MAT021',1,  500.0000, 90,40);

    ;WITH MaterialsWithoutInventory AS
    (
        SELECT m.id, m.code
        FROM materials m
        INNER JOIN @MaterialSeed ms
            ON ms.code = m.code
        WHERE NOT EXISTS
        (
            SELECT 1
            FROM inventories i
            WHERE i.material_id = m.id
        )
    )
    INSERT INTO inventories
        (material_id, quantity, expiry_date, created_at)
    SELECT
        mwi.id,
        ins.quantity,
        CASE
            WHEN ins.expiry_days_from_today IS NULL THEN NULL
            ELSE DATEADD(DAY, ins.expiry_days_from_today, @Today)
        END,
        DATEADD(DAY, -ins.created_days_ago, @NowUtc)
    FROM @InventorySeed ins
    INNER JOIN MaterialsWithoutInventory mwi
        ON mwi.code = ins.material_code;

    /* ======================================================================
       10. 庫存異動紀錄
       ====================================================================== */
    DECLARE @InventoryLogSeed TABLE
    (
        material_code varchar(50),
        quantity decimal(18,4),
        action varchar(50),
        note nvarchar(255),
        created_days_ago int
    );

    INSERT INTO @InventoryLogSeed
        (material_code, quantity, action, note, created_days_ago)
    VALUES
        ('MAT001', 10000.0000,'STOCK_IN',     N'[假資料] 阿薩姆紅茶葉進貨',       20),
        ('MAT002', 12000.0000,'STOCK_IN',     N'[假資料] 茉香綠茶葉進貨',         18),
        ('MAT003',  6500.0000,'STOCK_IN',     N'[假資料] 四季春茶葉進貨',         17),
        ('MAT005', 11000.0000,'STOCK_IN',     N'[假資料] 鮮奶冷藏進貨',            2),
        ('MAT008',  5000.0000,'STOCK_IN',     N'[假資料] 黑糖珍珠進貨',            8),
        ('MAT016',  1000.0000,'STOCK_IN',     N'[假資料] 飲料杯入庫',             14),
        ('MAT001', -1200.0000,'MANUAL_USE',   N'[假資料] 早班開店備茶',            1),
        ('MAT002',  -950.0000,'MANUAL_USE',   N'[假資料] 午班綠茶備料',            1),
        ('MAT005', -1800.0000,'MANUAL_USE',   N'[假資料] 鮮奶飲品備料',            1),
        ('MAT006',  -600.0000,'MANUAL_USE',   N'[假資料] 奶茶粉備料',              1),
        ('MAT008',  -700.0000,'MANUAL_USE',   N'[假資料] 珍珠煮製領料',            1),
        ('MAT011',  -350.0000,'MANUAL_USE',   N'[假資料] 檸檬飲品備料',            1),
        ('MAT008',  -150.0000,'WASTE',        N'[假資料] 珍珠煮製失敗耗損',        0),
        ('MAT005',  -300.0000,'EXPIRED',      N'[假資料] 鮮奶逾期報廢',            0),
        ('MAT011',  -200.0000,'ADJUSTMENT_OUT',N'[假資料] 盤點短少調整',           0),
        ('MAT016',    50.0000,'ADJUSTMENT_IN', N'[假資料] 盤點溢出調整',           0);

    INSERT INTO inventory_logs
        (material_id, quantity, action, ref_id, note, created_at)
    SELECT
        m.id,
        ils.quantity,
        ils.action,
        NULL,
        ils.note,
        DATEADD(DAY, -ils.created_days_ago, @NowUtc)
    FROM @InventoryLogSeed ils
    INNER JOIN materials m
        ON m.code = ils.material_code
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM inventory_logs il
        WHERE il.material_id = m.id
          AND il.action = ils.action
          AND il.note = ils.note
    );

    /* ======================================================================
       11. 採購單（30 筆）

       PurchaseOrdersStatus：
       DRAFT / PENDING_APPROVAL / APPROVED / REJECTED / ORDERED /
       PARTIALLY_RECEIVED / RECEIVED / CANCELLED
       ====================================================================== */
    DECLARE @PurchaseOrderSeed TABLE
    (
        order_no varchar(40) PRIMARY KEY,
        supplier_email varchar(50),
        order_status varchar(50),
        creator_username varchar(50),
        approver_username varchar(50),
        receiver_username varchar(50) NULL,
        created_days_ago int,
        updated_days_ago int,
        expected_day_offset int NULL,
        received_days_ago int NULL,
        receipt_url varchar(500) NULL,
        decision_remark nvarchar(1000) NULL
    );

    INSERT INTO @PurchaseOrderSeed
        (order_no, supplier_email, order_status, creator_username, approver_username,
         receiver_username, created_days_ago, updated_days_ago,
         expected_day_offset, received_days_ago, receipt_url, decision_remark)
    VALUES
        ('DEMO-PO-202609-001','tea01@erp-supplier.com',     'DRAFT',              'staff01','store_manager01', NULL,      1, 0,  7,NULL,NULL,N'草稿：補充阿薩姆茶葉'),
        ('DEMO-PO-202609-002','tea02@erp-supplier.com',     'PENDING_APPROVAL',   'staff02','store_manager01', NULL,      2, 1,  5,NULL,NULL,N'等待店長審核'),
        ('DEMO-PO-202609-003','tea03@erp-supplier.com',     'APPROVED',           'staff03','deputy_manager01',NULL,      3, 2,  4,NULL,NULL,N'價格與數量已確認'),
        ('DEMO-PO-202609-004','tea04@erp-supplier.com',     'REJECTED',           'staff01','store_manager01', NULL,      4, 3,  3,NULL,NULL,N'報價高於預算，請重新詢價'),
        ('DEMO-PO-202609-005','powder05@erp-supplier.com',  'ORDERED',            'staff02','deputy_manager01',NULL,      5, 3,  2,NULL,NULL,N'已向供應商下單'),
        ('DEMO-PO-202609-006','milk06@erp-supplier.com',    'PARTIALLY_RECEIVED', 'staff03','store_manager01', 'staff04', 6, 1,  1,   1,'/uploads/demo/receipts/po006.pdf',N'部分鮮奶已到貨'),
        ('DEMO-PO-202609-007','milk07@erp-supplier.com',    'RECEIVED',           'staff01','deputy_manager01','staff04', 8, 1, -2,   1,'/uploads/demo/receipts/po007.pdf',N'數量與效期確認完成'),
        ('DEMO-PO-202609-008','sugar08@erp-supplier.com',   'CANCELLED',          'staff02','store_manager01', NULL,     10, 8, -3,NULL,NULL,N'需求異動，取消採購'),
        ('DEMO-PO-202609-009','sugar09@erp-supplier.com',   'DRAFT',              'staff03','deputy_manager01',NULL,      1, 0,  8,NULL,NULL,N'草稿：黑糖原料補貨'),
        ('DEMO-PO-202609-010','pearl10@erp-supplier.com',   'PENDING_APPROVAL',   'staff01','store_manager01', NULL,      2, 1,  6,NULL,NULL,N'旺季前提高珍珠庫存'),
        ('DEMO-PO-202609-011','jelly11@erp-supplier.com',   'APPROVED',           'staff02','deputy_manager01',NULL,      4, 2,  5,NULL,NULL,N'核准椰果補貨'),
        ('DEMO-PO-202609-012','grass12@erp-supplier.com',   'REJECTED',           'staff03','store_manager01', NULL,      6, 5,  4,NULL,NULL,N'請調整最低訂購量'),
        ('DEMO-PO-202609-013','pudding13@erp-supplier.com', 'ORDERED',            'staff01','deputy_manager01',NULL,      7, 4,  2,NULL,NULL,N'已完成下單'),
        ('DEMO-PO-202609-014','aloe14@erp-supplier.com',    'PARTIALLY_RECEIVED', 'staff02','store_manager01', 'staff05', 9, 2, -1,   2,'/uploads/demo/receipts/po014.pdf',N'尚有一箱未到貨'),
        ('DEMO-PO-202609-015','lemon15@erp-supplier.com',   'RECEIVED',           'staff03','deputy_manager01','staff05',12, 2, -5,   2,'/uploads/demo/receipts/po015.pdf',N'檸檬原料完成收貨'),
        ('DEMO-PO-202609-016','passion16@erp-supplier.com', 'CANCELLED',          'staff01','store_manager01', NULL,     11, 9, -4,NULL,NULL,N'供應商無法如期交貨'),
        ('DEMO-PO-202609-017','mango17@erp-supplier.com',   'PENDING_APPROVAL',   'staff02','deputy_manager01',NULL,      3, 2,  7,NULL,NULL,N'新供應商首次採購待審'),
        ('DEMO-PO-202609-018','cup18@erp-supplier.com',     'APPROVED',           'staff03','store_manager01', NULL,      5, 3,  6,NULL,NULL,N'包材安全庫存補貨'),
        ('DEMO-PO-202609-019','film19@erp-supplier.com',    'ORDERED',            'staff01','deputy_manager01',NULL,      6, 4,  3,NULL,NULL,N'封口膜已下單'),
        ('DEMO-PO-202609-020','straw20@erp-supplier.com',   'RECEIVED',           'staff02','store_manager01', 'staff04',14, 1, -7,   1,'/uploads/demo/receipts/po020.pdf',N'吸管已全數入庫'),
        ('DEMO-PO-202609-021','north21@erp-supplier.com',   'DRAFT',              'staff03','deputy_manager01',NULL,      0, 0, 10,NULL,NULL,N'草稿：北區綜合補貨'),
        ('DEMO-PO-202609-022','central22@erp-supplier.com', 'PENDING_APPROVAL',   'staff01','store_manager01', NULL,      2, 1,  8,NULL,NULL,N'中區配送訂單待核准'),
        ('DEMO-PO-202609-023','south23@erp-supplier.com',   'APPROVED',           'staff02','deputy_manager01',NULL,      4, 2,  7,NULL,NULL,N'冷鏈配送費已確認'),
        ('DEMO-PO-202609-024','east24@erp-supplier.com',    'REJECTED',           'staff03','store_manager01', NULL,      8, 7,  5,NULL,NULL,N'運費超出預算'),
        ('DEMO-PO-202609-025','coffee25@erp-supplier.com',  'ORDERED',            'staff01','deputy_manager01',NULL,      9, 6,  4,NULL,NULL,N'咖啡豆已安排烘焙'),
        ('DEMO-PO-202609-026','cocoa26@erp-supplier.com',   'PARTIALLY_RECEIVED', 'staff02','store_manager01', 'staff05',10, 2, -2,   2,'/uploads/demo/receipts/po026.pdf',N'巧克力醬部分到貨'),
        ('DEMO-PO-202609-027','trial27@erp-supplier.com',   'RECEIVED',           'staff03','deputy_manager01','staff04',15, 2, -8,   2,'/uploads/demo/receipts/po027.pdf',N'試單已完成驗收'),
        ('DEMO-PO-202609-028','delay28@erp-supplier.com',   'CANCELLED',          'staff01','store_manager01', NULL,     16,10, -9,NULL,NULL,N'因延遲交貨取消訂單'),
        ('DEMO-PO-202609-029','tea01@erp-supplier.com',     'PENDING_APPROVAL',   'staff02','deputy_manager01',NULL,      1, 0,  9,NULL,NULL,N'月底茶葉補貨待審'),
        ('DEMO-PO-202609-030','milk06@erp-supplier.com',    'RECEIVED',           'staff03','store_manager01', 'staff05',18, 1,-10,   1,'/uploads/demo/receipts/po030.pdf',N'鮮奶冷鏈驗收完成');

    INSERT INTO purchase_orders
        (order_number, supplier_id, status, created_by_user_id,
         approved_by_user_id, received_by_user_id, total,
         created_at, updated_at, expected_delivery_date,
         received_at, receipt_url, decision_remark)
    SELECT
        pos.order_no,
        s.id,
        pos.order_status,
        creator.id,
        approver.id,
        receiver.id,
        0.00,
        DATEADD(DAY, -pos.created_days_ago, @NowLocal),
        DATEADD(DAY, -pos.updated_days_ago, @NowLocal),
        CASE
            WHEN pos.expected_day_offset IS NULL THEN NULL
            ELSE DATEADD(DAY, pos.expected_day_offset, @Today)
        END,
        CASE
            WHEN pos.received_days_ago IS NULL THEN NULL
            ELSE DATEADD(DAY, -pos.received_days_ago, @NowLocal)
        END,
        pos.receipt_url,
        pos.decision_remark
    FROM @PurchaseOrderSeed pos
    INNER JOIN suppliers s
        ON s.email = pos.supplier_email
    INNER JOIN users creator
        ON creator.username = pos.creator_username
    INNER JOIN users approver
        ON approver.username = pos.approver_username
    LEFT JOIN users receiver
        ON receiver.username = pos.receiver_username
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM purchase_orders po
        WHERE po.order_number = pos.order_no
    );

    /* ======================================================================
       12. 採購單明細
       每張採購單自動建立 3 筆不同原物料明細。
       ====================================================================== */
    ;WITH PurchaseOrderNumbers AS
    (
        SELECT
            pos.order_no,
            ROW_NUMBER() OVER (ORDER BY pos.order_no) AS row_no
        FROM @PurchaseOrderSeed pos
    ),
    MaterialNumbers AS
    (
        SELECT
            ms.code,
            ms.purchase_cost,
            ROW_NUMBER() OVER (ORDER BY ms.code) AS row_no
        FROM @MaterialSeed ms
        WHERE ms.material_status = 'ACTIVE'
          AND ms.cost_mode = 'CONVERSION'
    )
    INSERT INTO purchase_order_items
        (purchase_order_id, material_id, quantity, price)
    SELECT
        po.id,
        m.id,
        CAST(1 + ((pon.row_no + line.line_no) % 8) AS decimal(18,4)),
        mn.purchase_cost
    FROM PurchaseOrderNumbers pon
    INNER JOIN purchase_orders po
        ON po.order_number = pon.order_no
    CROSS JOIN (VALUES (0), (1), (2)) line(line_no)
    INNER JOIN MaterialNumbers mn
        ON mn.row_no = ((pon.row_no + line.line_no * 5 - 1) % 18) + 1
    INNER JOIN materials m
        ON m.code = mn.code
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM purchase_order_items poi
        WHERE poi.purchase_order_id = po.id
          AND poi.material_id = m.id
    );

    -- 依實際明細重新計算採購單總額，避免手寫總額與明細不一致。
    UPDATE po
    SET po.total = totals.total_amount
    FROM purchase_orders po
    INNER JOIN
    (
        SELECT
            poi.purchase_order_id,
            SUM(poi.quantity * poi.price) AS total_amount
        FROM purchase_order_items poi
        GROUP BY poi.purchase_order_id
    ) totals
        ON totals.purchase_order_id = po.id
    INNER JOIN @PurchaseOrderSeed pos
        ON pos.order_no = po.order_number;

    /* ======================================================================
       13. 銷售單（36 筆）

       SalesOrderStatus：COMPLETED / VOIDED
       PaymentMethod：CASH / CREDIT_CARD / MOBILE_PAYMENT
       ====================================================================== */
    DECLARE @NumberSeed TABLE
    (
        n int PRIMARY KEY
    );

    INSERT INTO @NumberSeed (n)
    VALUES
        (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),
        (13),(14),(15),(16),(17),(18),(19),(20),(21),(22),(23),(24),
        (25),(26),(27),(28),(29),(30),(31),(32),(33),(34),(35),(36);

    INSERT INTO sales_orders
        (order_number, status, payment_method, total_amount,
         created_by_user_id, voided_by_user_id, created_at, voided_at,
         note, void_reason)
    SELECT
        CONCAT('DEMO-SO-', RIGHT('000' + CONVERT(varchar(3), ns.n), 3)),
        CASE
            WHEN ns.n IN (8, 17, 29) THEN 'VOIDED'
            ELSE 'COMPLETED'
        END,
        CASE ns.n % 3
            WHEN 1 THEN 'CASH'
            WHEN 2 THEN 'CREDIT_CARD'
            ELSE 'MOBILE_PAYMENT'
        END,
        0.00,
        creator.id,
        CASE
            WHEN ns.n IN (8, 17, 29) THEN voider.id
            ELSE NULL
        END,
        DATEADD(MINUTE, -(ns.n * 23), DATEADD(DAY, -((ns.n - 1) % 18), @NowLocal)),
        CASE
            WHEN ns.n IN (8, 17, 29)
                THEN DATEADD(MINUTE, 30, DATEADD(MINUTE, -(ns.n * 23), DATEADD(DAY, -((ns.n - 1) % 18), @NowLocal)))
            ELSE NULL
        END,
        CASE ns.n % 3
            WHEN 1 THEN N'[假資料] 門市現金交易'
            WHEN 2 THEN N'[假資料] 信用卡交易'
            ELSE N'[假資料] 行動支付交易'
        END,
        CASE
            WHEN ns.n IN (8, 17, 29) THEN N'顧客取消訂單，測試作廢流程'
            ELSE NULL
        END
    FROM @NumberSeed ns
    INNER JOIN users creator
        ON creator.username =
            CASE ns.n % 4
                WHEN 0 THEN 'staff01'
                WHEN 1 THEN 'staff02'
                WHEN 2 THEN 'staff03'
                ELSE 'pt01'
            END
    INNER JOIN users voider
        ON voider.username = 'store_manager01'
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM sales_orders so
        WHERE so.order_number = CONCAT('DEMO-SO-', RIGHT('000' + CONVERT(varchar(3), ns.n), 3))
    );

    /* ======================================================================
       14. 銷售單明細
       一般訂單 2 筆明細；每 3 張中的第 3 張會有 3 筆明細。
       ====================================================================== */
    ;WITH ActiveProductNumbers AS
    (
        SELECT
            p.id,
            p.sku,
            p.name,
            p.selling_price,
            ROW_NUMBER() OVER (ORDER BY p.sku) AS row_no
        FROM products p
        INNER JOIN @ProductSeed ps
            ON ps.sku = p.sku
        WHERE ps.product_status = 'ACTIVE'
    ),
    SalesOrderNumbers AS
    (
        SELECT
            ns.n,
            CONCAT('DEMO-SO-', RIGHT('000' + CONVERT(varchar(3), ns.n), 3)) AS order_no
        FROM @NumberSeed ns
    )
    INSERT INTO sales_order_items
        (sales_order_id, product_id, product_sku, product_name,
         quantity, unit_price, subtotal)
    SELECT
        so.id,
        apn.id,
        apn.sku,
        apn.name,
        CAST(1 + ((son.n + line.line_no) % 3) AS decimal(18,4)),
        apn.selling_price,
        apn.selling_price * CAST(1 + ((son.n + line.line_no) % 3) AS decimal(18,4))
    FROM SalesOrderNumbers son
    INNER JOIN sales_orders so
        ON so.order_number = son.order_no
    CROSS JOIN (VALUES (0), (1), (2)) line(line_no)
    INNER JOIN ActiveProductNumbers apn
        ON apn.row_no = ((son.n + line.line_no * 7 - 1) % 20) + 1
    WHERE line.line_no < CASE WHEN son.n % 3 = 0 THEN 3 ELSE 2 END
      AND NOT EXISTS
    (
        SELECT 1
        FROM sales_order_items soi
        WHERE soi.sales_order_id = so.id
          AND soi.product_id = apn.id
    );

    -- 依明細小計回寫銷售單總額。
    UPDATE so
    SET so.total_amount = totals.total_amount
    FROM sales_orders so
    INNER JOIN
    (
        SELECT
            soi.sales_order_id,
            SUM(soi.subtotal) AS total_amount
        FROM sales_order_items soi
        GROUP BY soi.sales_order_id
    ) totals
        ON totals.sales_order_id = so.id
    WHERE so.order_number LIKE 'DEMO-SO-%';

    /* ======================================================================
       15. 請假單
       LeaveType：ANNUAL / SICK / PERSONAL / MARRIAGE
       LeaveStatus：DRAFT / PENDING / APPROVED / REJECTED / CANCELLED
       ====================================================================== */
    DECLARE @LeaveSeed TABLE
    (
        username varchar(50),
        leave_type varchar(30),
        start_day_offset int,
        leave_days int,
        reason nvarchar(500),
        leave_status varchar(30),
        created_days_ago int
    );

    INSERT INTO @LeaveSeed
        (username, leave_type, start_day_offset, leave_days,
         reason, leave_status, created_days_ago)
    VALUES
        ('staff01','ANNUAL',   12,2,N'[假資料] 家庭旅遊',            'PENDING',    3),
        ('staff02','SICK',     -8,1,N'[假資料] 身體不適就醫',        'APPROVED',  10),
        ('staff03','PERSONAL',  5,1,N'[假資料] 辦理個人事務',        'DRAFT',      1),
        ('staff04','ANNUAL',   20,3,N'[假資料] 年度休假安排',        'APPROVED',   6),
        ('staff05','PERSONAL',  7,1,N'[假資料] 家庭重要行程',        'REJECTED',   4),
        ('pt01',   'SICK',     -3,1,N'[假資料] 感冒休養',            'APPROVED',   5),
        ('pt02',   'PERSONAL',  9,1,N'[假資料] 學校課程活動',        'PENDING',    2),
        ('team_leader01','MARRIAGE',30,5,N'[假資料] 婚假申請',       'PENDING',    7),
        ('shift_leader01','ANNUAL',15,2,N'[假資料] 返鄉探親',        'CANCELLED',  5),
        ('assistant_leader01','PERSONAL',3,1,N'[假資料] 銀行辦事',   'DRAFT',      1);

    INSERT INTO leave_requests
        (applicant_id, leave_type, start_date, end_date,
         reason, status, created_at)
    SELECT
        u.id,
        ls.leave_type,
        DATEADD(DAY, ls.start_day_offset, @Today),
        DATEADD(DAY, ls.start_day_offset + ls.leave_days - 1, @Today),
        ls.reason,
        ls.leave_status,
        DATEADD(DAY, -ls.created_days_ago, @NowUtc)
    FROM @LeaveSeed ls
    INNER JOIN users u
        ON u.username = ls.username
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM leave_requests lr
        WHERE lr.applicant_id = u.id
          AND lr.reason = ls.reason
    );

    /* ======================================================================
       16. 簽核流程

       Workflow 的狀態只有：PENDING / APPROVED / REJECTED / CANCELLED。
       因此 ORDERED、PARTIALLY_RECEIVED、RECEIVED 均代表原簽核已 APPROVED。
       ====================================================================== */
    INSERT INTO workflows
        (document_type, document_id, status, applicant_id, approver_id, created_at)
    SELECT
        'ORDER',
        po.id,
        CASE
            WHEN po.status = 'PENDING_APPROVAL' THEN 'PENDING'
            WHEN po.status = 'REJECTED' THEN 'REJECTED'
            WHEN po.status = 'CANCELLED' THEN 'CANCELLED'
            ELSE 'APPROVED'
        END,
        po.created_by_user_id,
        po.approved_by_user_id,
        DATEADD(MINUTE, 5, po.created_at)
    FROM purchase_orders po
    INNER JOIN @PurchaseOrderSeed pos
        ON pos.order_no = po.order_number
    WHERE po.status <> 'DRAFT'
      AND NOT EXISTS
    (
        SELECT 1
        FROM workflows w
        WHERE w.document_type = 'ORDER'
          AND w.document_id = po.id
    );

    INSERT INTO workflows
        (document_type, document_id, status, applicant_id, approver_id, created_at)
    SELECT
        'LEAVE',
        lr.id,
        CASE lr.status
            WHEN 'PENDING' THEN 'PENDING'
            WHEN 'APPROVED' THEN 'APPROVED'
            WHEN 'REJECTED' THEN 'REJECTED'
            ELSE 'CANCELLED'
        END,
        lr.applicant_id,
        approver.id,
        DATEADD(MINUTE, 5, lr.created_at)
    FROM leave_requests lr
    INNER JOIN @LeaveSeed ls
        ON ls.reason = lr.reason
    INNER JOIN users approver
        ON approver.username = 'store_manager01'
    WHERE lr.status <> 'DRAFT'
      AND NOT EXISTS
    (
        SELECT 1
        FROM workflows w
        WHERE w.document_type = 'LEAVE'
          AND w.document_id = lr.id
    );

    /* ======================================================================
       17. 簽核紀錄
       ====================================================================== */
    INSERT INTO workflow_logs
        (workflow_id, action, operator_id, remark, created_at)
    SELECT
        w.id,
        'SUBMIT',
        w.applicant_id,
        CASE
            WHEN w.document_type = 'ORDER' THEN N'[假資料] 送出採購單簽核'
            ELSE N'[假資料] 送出請假單簽核'
        END,
        w.created_at
    FROM workflows w
    WHERE
        (
            (
                w.document_type = 'ORDER'
                AND EXISTS
                (
                    SELECT 1
                    FROM purchase_orders po
                    INNER JOIN @PurchaseOrderSeed pos
                        ON pos.order_no = po.order_number
                    WHERE po.id = w.document_id
                )
            )
            OR
            (
                w.document_type = 'LEAVE'
                AND EXISTS
                (
                    SELECT 1
                    FROM leave_requests lr
                    INNER JOIN @LeaveSeed ls
                        ON ls.reason = lr.reason
                    WHERE lr.id = w.document_id
                )
            )
        )
        AND NOT EXISTS
        (
            SELECT 1
            FROM workflow_logs wl
            WHERE wl.workflow_id = w.id
              AND wl.action = 'SUBMIT'
        );

    INSERT INTO workflow_logs
        (workflow_id, action, operator_id, remark, created_at)
    SELECT
        w.id,
        CASE w.status
            WHEN 'APPROVED' THEN 'APPROVE'
            WHEN 'REJECTED' THEN 'REJECT'
            ELSE 'CANCEL'
        END,
        CASE
            WHEN w.status = 'CANCELLED' THEN w.applicant_id
            ELSE w.approver_id
        END,
        CASE w.status
            WHEN 'APPROVED' THEN N'[假資料] 簽核通過'
            WHEN 'REJECTED' THEN N'[假資料] 簽核駁回，請修改後重新送出'
            ELSE N'[假資料] 申請人取消簽核'
        END,
        DATEADD(HOUR, 2, w.created_at)
    FROM workflows w
    WHERE w.status <> 'PENDING'
      AND
      (
          (
              w.document_type = 'ORDER'
              AND EXISTS
              (
                  SELECT 1
                  FROM purchase_orders po
                  INNER JOIN @PurchaseOrderSeed pos
                      ON pos.order_no = po.order_number
                  WHERE po.id = w.document_id
              )
          )
          OR
          (
              w.document_type = 'LEAVE'
              AND EXISTS
              (
                  SELECT 1
                  FROM leave_requests lr
                  INNER JOIN @LeaveSeed ls
                      ON ls.reason = lr.reason
                  WHERE lr.id = w.document_id
              )
          )
      )
      AND NOT EXISTS
      (
          SELECT 1
          FROM workflow_logs wl
          WHERE wl.workflow_id = w.id
            AND wl.action =
                CASE w.status
                    WHEN 'APPROVED' THEN 'APPROVE'
                    WHEN 'REJECTED' THEN 'REJECT'
                    ELSE 'CANCEL'
                END
      );

    /* ======================================================================
       18. 通知
       category：inventory / workflow / supplier / security
       type：info / success / warning / danger
       ====================================================================== */
    DECLARE @NotificationSeed TABLE
    (
        username varchar(50),
        title nvarchar(150),
        content nvarchar(1000),
        category varchar(30),
        notification_type varchar(20),
        action_route varchar(255),
        is_read bit,
        created_days_ago int
    );

    INSERT INTO @NotificationSeed
        (username, title, content, category, notification_type,
         action_route, is_read, created_days_ago)
    VALUES
        ('store_manager01',N'黑糖珍珠庫存偏低',N'黑糖珍珠低於安全庫存，請確認是否補貨。','inventory','warning','/inventory',0,0),
        ('store_manager01',N'待簽核採購單',N'目前有新的採購單等待審核。','workflow','info','/purchase-orders',0,0),
        ('store_manager01',N'待簽核請假單',N'目前有新的請假申請等待處理。','workflow','info','/leave',0,0),
        ('purchase_manager01',N'供應商交貨異常',N'延遲交貨測試供應商目前已暫停交易。','supplier','danger','/suppliers',0,1),
        ('staff01',N'採購單已核准',N'你的採購單已通過主管簽核。','workflow','success','/purchase-orders',1,1),
        ('staff02',N'採購單待補件',N'有一張採購單遭駁回，請查看原因。','workflow','warning','/purchase-orders',0,2),
        ('staff03',N'收貨提醒',N'今日有採購品項預計到貨。','inventory','info','/purchase-orders',0,0),
        ('sales_manager01',N'昨日銷售摘要',N'昨日銷售單資料已完成彙整。','workflow','success','/sales-orders',1,1),
        ('deputy_manager01',N'鮮奶即將到期',N'部分鮮奶批次將於五日內到期。','inventory','warning','/inventory',0,0),
        ('pt01',N'請假單已核准',N'你的病假申請已通過。','workflow','success','/leave',1,2),
        ('staff05',N'請假單遭駁回',N'請假申請遭駁回，請與主管確認。','workflow','danger','/leave',0,1),
        ('store_manager01',N'帳號安全提醒',N'偵測到一個鎖定中的測試帳號。','security','warning','/users',0,0);

    INSERT INTO notification_record
        (user_id, title, content, category, type,
         action_route, is_read, created_at)
    SELECT
        u.id,
        ns.title,
        ns.content,
        ns.category,
        ns.notification_type,
        ns.action_route,
        ns.is_read,
        DATEADD(DAY, -ns.created_days_ago, @NowUtc)
    FROM @NotificationSeed ns
    INNER JOIN users u
        ON u.username = ns.username
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM notification_record nr
        WHERE nr.user_id = u.id
          AND nr.title = ns.title
    );

    /* ======================================================================
       19. 打卡紀錄
       clock_type：CLOCK_IN / CLOCK_OUT
       ====================================================================== */
    DECLARE @ClockUserSeed TABLE
    (
        username varchar(50) PRIMARY KEY,
        minute_offset int
    );

    INSERT INTO @ClockUserSeed (username, minute_offset)
    VALUES
        ('staff01',  0),
        ('staff02',  6),
        ('staff03', 12),
        ('staff04', 18),
        ('staff05', 24),
        ('pt01',    30),
        ('pt02',    36);

    DECLARE @ClockDaySeed TABLE
    (
        days_ago int PRIMARY KEY
    );

    INSERT INTO @ClockDaySeed (days_ago)
    VALUES (1),(2),(3),(4),(5);

    ;WITH ClockRows AS
    (
        SELECT
            CONVERT(varchar(50), u.id) AS user_id,
            DATEADD
            (
                MINUTE,
                8 * 60 + cus.minute_offset,
                CAST(DATEADD(DAY, -cds.days_ago, @Today) AS datetime2)
            ) AS clock_time,
            'CLOCK_IN' AS clock_type
        FROM @ClockUserSeed cus
        INNER JOIN users u
            ON u.username = cus.username
        CROSS JOIN @ClockDaySeed cds

        UNION ALL

        SELECT
            CONVERT(varchar(50), u.id),
            DATEADD
            (
                MINUTE,
                17 * 60 + 30 + cus.minute_offset,
                CAST(DATEADD(DAY, -cds.days_ago, @Today) AS datetime2)
            ),
            'CLOCK_OUT'
        FROM @ClockUserSeed cus
        INNER JOIN users u
            ON u.username = cus.username
        CROSS JOIN @ClockDaySeed cds
    )
    INSERT INTO clock_records (user_id, clock_time, clock_type)
    SELECT cr.user_id, cr.clock_time, cr.clock_type
    FROM ClockRows cr
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM clock_records existing_record
        WHERE existing_record.user_id = cr.user_id
          AND existing_record.clock_time = cr.clock_time
          AND existing_record.clock_type = cr.clock_type
    );

    /* ======================================================================
       20. 行事曆事件與參與者
       ====================================================================== */
    DECLARE @CalendarEventSeed TABLE
    (
        event_id varchar(36) PRIMARY KEY,
        title nvarchar(255),
        description nvarchar(1000),
        category varchar(50),
        day_offset int,
        start_time time,
        end_time time,
        location nvarchar(255),
        organizer nvarchar(255),
        priority varchar(20),
        event_status varchar(30),
        related_ref varchar(255) NULL,
        reminder_minutes int
    );

    INSERT INTO @CalendarEventSeed
        (event_id, title, description, category, day_offset,
         start_time, end_time, location, organizer,
         priority, event_status, related_ref, reminder_minutes)
    VALUES
        ('10000000-0000-0000-0000-000000000001',N'茶葉採購到貨',N'確認茶葉品項、數量與效期。','procurement',1,'09:00','10:00',N'門市倉庫',N'王建國','high','pending','DEMO-PO-202609-003',30),
        ('10000000-0000-0000-0000-000000000002',N'鮮奶批次盤點',N'盤點冷藏鮮奶並確認即期品。','maintenance',0,'14:00','15:00',N'冷藏庫',N'劉冠廷','high','in_progress',NULL,15),
        ('10000000-0000-0000-0000-000000000003',N'每週營運會議',N'檢討銷售、庫存與人力安排。','meeting',2,'10:00','11:30',N'會議室',N'陳志明','medium','pending',NULL,30),
        ('10000000-0000-0000-0000-000000000004',N'新品試作',N'測試百香雙Q綠茶甜度與配方。','production',3,'13:30','15:30',N'中央廚房',N'張淑芬','medium','pending',NULL,60),
        ('10000000-0000-0000-0000-000000000005',N'設備定期保養',N'封口機與製冰機例行保養。','maintenance',5,'08:00','10:00',N'門市吧台',N'黃俊傑','high','pending',NULL,1440),
        ('10000000-0000-0000-0000-000000000006',N'中秋節行銷活動',N'規劃節慶限定飲品與宣傳素材。','marketing',7,'15:00','16:30',N'會議室',N'張淑芬','medium','pending',NULL,60),
        ('10000000-0000-0000-0000-000000000007',N'員工請假交接',N'確認請假期間的排班代理人。','leave',4,'16:00','16:30',N'店長室',N'林雅雯','low','pending',NULL,30),
        ('10000000-0000-0000-0000-000000000008',N'上週庫存盤點完成',N'上週庫存盤點與差異調整已完成。','maintenance',-3,'17:00','18:00',N'門市倉庫',N'劉冠廷','medium','completed',NULL,15);

    INSERT INTO calendar_events
        (id, title, description, category, date,
         start_time, end_time, location, organizer,
         priority, status, related_ref, reminder_minutes, created_at)
    SELECT
        ces.event_id,
        ces.title,
        ces.description,
        ces.category,
        DATEADD(DAY, ces.day_offset, @Today),
        ces.start_time,
        ces.end_time,
        ces.location,
        ces.organizer,
        ces.priority,
        ces.event_status,
        ces.related_ref,
        ces.reminder_minutes,
        @NowLocal
    FROM @CalendarEventSeed ces
    WHERE NOT EXISTS
    (
        SELECT 1
        FROM calendar_events ce
        WHERE ce.id = ces.event_id
    );

    DECLARE @EventAttendeeSeed TABLE
    (
        event_id varchar(36),
        attendee_name nvarchar(255),
        PRIMARY KEY (event_id, attendee_name)
    );

    INSERT INTO @EventAttendeeSeed (event_id, attendee_name)
    VALUES
        ('10000000-0000-0000-0000-000000000001',N'王建國'),
        ('10000000-0000-0000-0000-000000000001',N'王小明'),
        ('10000000-0000-0000-0000-000000000002',N'劉冠廷'),
        ('10000000-0000-0000-0000-000000000002',N'張志豪'),
        ('10000000-0000-0000-0000-000000000003',N'陳志明'),
        ('10000000-0000-0000-0000-000000000003',N'林雅雯'),
        ('10000000-0000-0000-0000-000000000003',N'王建國'),
        ('10000000-0000-0000-0000-000000000003',N'張淑芬'),
        ('10000000-0000-0000-0000-000000000004',N'張淑芬'),
        ('10000000-0000-0000-0000-000000000004',N'黃雅婷'),
        ('10000000-0000-0000-0000-000000000005',N'黃俊傑'),
        ('10000000-0000-0000-0000-000000000005',N'劉冠廷'),
        ('10000000-0000-0000-0000-000000000006',N'張淑芬'),
        ('10000000-0000-0000-0000-000000000006',N'李佳穎'),
        ('10000000-0000-0000-0000-000000000007',N'林雅雯'),
        ('10000000-0000-0000-0000-000000000007',N'王小明'),
        ('10000000-0000-0000-0000-000000000008',N'劉冠廷'),
        ('10000000-0000-0000-0000-000000000008',N'張志豪');

    INSERT INTO event_attendees (event_id, attendee_name)
    SELECT eas.event_id, eas.attendee_name
    FROM @EventAttendeeSeed eas
    WHERE EXISTS
    (
        SELECT 1
        FROM calendar_events ce
        WHERE ce.id = eas.event_id
    )
      AND NOT EXISTS
    (
        SELECT 1
        FROM event_attendees ea
        WHERE ea.event_id = eas.event_id
          AND ea.attendee_name = eas.attendee_name
    );

    COMMIT TRANSACTION;

    PRINT N'ERP 假資料建立完成。';

    /* 執行完成後回傳本腳本建立的主要資料筆數。 */
    SELECT
        (SELECT COUNT(*) FROM users u INNER JOIN @UserSeed us ON us.username = u.username) AS users_count,
        (SELECT COUNT(*) FROM materials m INNER JOIN @MaterialSeed ms ON ms.code = m.code) AS materials_count,
        (SELECT COUNT(*) FROM products p INNER JOIN @ProductSeed ps ON ps.sku = p.sku) AS products_count,
        (SELECT COUNT(*) FROM suppliers s INNER JOIN @SupplierSeed ss ON ss.email = s.email) AS suppliers_count,
        (SELECT COUNT(*) FROM purchase_orders po INNER JOIN @PurchaseOrderSeed pos ON pos.order_no = po.order_number) AS purchase_orders_count,
        (SELECT COUNT(*) FROM sales_orders so WHERE so.order_number LIKE 'DEMO-SO-%') AS sales_orders_count,
        (SELECT COUNT(*) FROM leave_requests lr INNER JOIN @LeaveSeed ls ON ls.reason = lr.reason) AS leave_requests_count,
        (SELECT COUNT(*) FROM calendar_events ce INNER JOIN @CalendarEventSeed ces ON ces.event_id = ce.id) AS calendar_events_count;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;
^^^
