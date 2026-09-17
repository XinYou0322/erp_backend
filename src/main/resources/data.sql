/* =============================================================
   飲料店 ERP - 完整開發假資料
   依 demo.zip 內 @Entity 掃描結果產生
   SQL Server / Spring Boot data.sql

   警告：每次執行都會刪除下列資料表的舊資料。
   僅建議用於開發環境。
   ============================================================= */
SET NOCOUNT ON;

/* ---------- 1. DELETE：子表 -> 父表 ---------- */
DELETE FROM workflow_logs;
DELETE FROM workflows;
DELETE FROM leave_requests;
DELETE FROM purchase_order_items;
DELETE FROM purchase_orders;
DELETE FROM supplier_note;
DELETE FROM sales_order_items;
DELETE FROM sales_orders;
DELETE FROM notification_record;
DELETE FROM inventory_logs;
DELETE FROM inventories;
DELETE FROM bom;
DELETE FROM products;
DELETE FROM materials;
DELETE FROM product_categories;
DELETE FROM suppliers;
DELETE FROM users;
DELETE FROM roles;

/* ---------- 2. IDENTITY RESEED ---------- */
DBCC CHECKIDENT ('workflow_logs', RESEED, 0);
DBCC CHECKIDENT ('workflows', RESEED, 0);
DBCC CHECKIDENT ('leave_requests', RESEED, 0);
DBCC CHECKIDENT ('purchase_order_items', RESEED, 0);
DBCC CHECKIDENT ('purchase_orders', RESEED, 0);
DBCC CHECKIDENT ('supplier_note', RESEED, 0);
DBCC CHECKIDENT ('sales_order_items', RESEED, 0);
DBCC CHECKIDENT ('sales_orders', RESEED, 0);
DBCC CHECKIDENT ('notification_record', RESEED, 0);
DBCC CHECKIDENT ('inventory_logs', RESEED, 0);
DBCC CHECKIDENT ('inventories', RESEED, 0);
DBCC CHECKIDENT ('bom', RESEED, 0);
DBCC CHECKIDENT ('products', RESEED, 0);
DBCC CHECKIDENT ('materials', RESEED, 0);
DBCC CHECKIDENT ('product_categories', RESEED, 0);
DBCC CHECKIDENT ('suppliers', RESEED, 0);
DBCC CHECKIDENT ('users', RESEED, 0);
DBCC CHECKIDENT ('roles', RESEED, 0);

/* ---------- 3. Roles ---------- */
INSERT INTO roles (name, description) VALUES
('ADMIN', N'系統管理員'),
('MANAGER', N'店長 / 簽核主管'),
('STAFF', N'一般門市員工'),
('PURCHASING', N'採購人員');

/* ---------- 4. Users ----------
   password 欄位放 BCrypt 格式測試值；若要實際登入，建議透過你的註冊/API
   建立測試帳號，以確保密碼與目前 PasswordEncoder 完全一致。
*/
INSERT INTO users (username, password, name, email, role_id, status, created_at)
SELECT 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'系統管理員', 'admin@drinkerp.test', id, 'ACTIVE', SYSUTCDATETIME()
FROM roles WHERE name='ADMIN';
INSERT INTO users (username, password, name, email, role_id, status, created_at)
SELECT 'manager01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'王店長', 'manager@drinkerp.test', id, 'ACTIVE', SYSUTCDATETIME()
FROM roles WHERE name='MANAGER';
INSERT INTO users (username, password, name, email, role_id, status, created_at)
SELECT 'staff01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'陳小明', 'staff01@drinkerp.test', id, 'ACTIVE', SYSUTCDATETIME()
FROM roles WHERE name='STAFF';
INSERT INTO users (username, password, name, email, role_id, status, created_at)
SELECT 'buyer01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'林採購', 'buyer@drinkerp.test', id, 'ACTIVE', SYSUTCDATETIME()
FROM roles WHERE name='PURCHASING';

/* ---------- 5. ProductCategory ---------- */
INSERT INTO product_categories (name, active) VALUES
(N'茶類',1),(N'奶茶類',1),(N'特調類',1),(N'季節限定',0);

/* ---------- 6. Materials ---------- */
INSERT INTO materials
(code,name,unit,cost_mode,purchase_unit,status,conversion_quantity,purchase_cost,cost,safety_stock)
VALUES
('M001',N'阿薩姆紅茶葉',N'g','CONVERSION',N'包','ACTIVE',2500,800,0.3200,5000),
('M002',N'茉香綠茶葉',N'g','CONVERSION',N'包','ACTIVE',2500,750,0.3000,5000),
('M003',N'鮮奶',N'ml','CONVERSION',N'瓶','ACTIVE',1858,150,0.0807,10000),
('M004',N'黑糖珍珠',N'g','CONVERSION',N'包','ACTIVE',3000,270,0.0900,6000),
('M005',N'蔗糖',N'g','CONVERSION',N'袋','ACTIVE',5000,200,0.0400,10000),
('M006',N'700ml飲料杯',N'個','CONVERSION',N'箱','ACTIVE',1000,1800,1.8000,300),
('M007',N'粗吸管',N'個','CONVERSION',N'箱','ACTIVE',2000,1000,0.5000,500),
('M008',N'檸檬汁',N'ml','DIRECT',NULL,'ACTIVE',NULL,NULL,0.1500,5000),
('M009',N'舊版奶精粉',N'g','CONVERSION',N'包','INACTIVE',1000,180,0.1800,2000);

/* ---------- 7. Products ---------- */
INSERT INTO products (sku,name,category_id,selling_price,cost_price,unit,status)
SELECT 'P001',N'阿薩姆紅茶',id,35,8.50,N'杯','ACTIVE' FROM product_categories WHERE name=N'茶類';
INSERT INTO products (sku,name,category_id,selling_price,cost_price,unit,status)
SELECT 'P002',N'茉香綠茶',id,35,8.20,N'杯','ACTIVE' FROM product_categories WHERE name=N'茶類';
INSERT INTO products (sku,name,category_id,selling_price,cost_price,unit,status)
SELECT 'P003',N'珍珠紅茶',id,50,13.50,N'杯','ACTIVE' FROM product_categories WHERE name=N'茶類';
INSERT INTO products (sku,name,category_id,selling_price,cost_price,unit,status)
SELECT 'P004',N'鮮奶茶',id,65,27,N'杯','ACTIVE' FROM product_categories WHERE name=N'奶茶類';
INSERT INTO products (sku,name,category_id,selling_price,cost_price,unit,status)
SELECT 'P005',N'珍珠鮮奶茶',id,75,32,N'杯','ACTIVE' FROM product_categories WHERE name=N'奶茶類';
INSERT INTO products (sku,name,category_id,selling_price,cost_price,unit,status)
SELECT 'P006',N'檸檬紅茶',id,55,15,N'杯','ACTIVE' FROM product_categories WHERE name=N'特調類';
INSERT INTO products (sku,name,category_id,selling_price,cost_price,unit,status)
SELECT 'P007',N'舊版奶精紅茶',id,45,12,N'杯','INACTIVE' FROM product_categories WHERE name=N'奶茶類';

/* ---------- 8. BOM ---------- */
INSERT INTO bom(product_id,material_id,quantity)
SELECT p.id,m.id,v.qty FROM (VALUES
('P001','M001',15.0000),('P001','M005',25.0000),('P001','M006',1.0000),
('P002','M002',15.0000),('P002','M005',25.0000),('P002','M006',1.0000),
('P003','M001',15.0000),('P003','M004',80.0000),('P003','M005',25.0000),('P003','M006',1.0000),
('P004','M001',15.0000),('P004','M003',250.0000),('P004','M005',20.0000),('P004','M006',1.0000),
('P005','M001',15.0000),('P005','M003',250.0000),('P005','M004',80.0000),('P005','M005',20.0000),('P005','M006',1.0000),
('P006','M001',15.0000),('P006','M008',40.0000),('P006','M005',25.0000),('P006','M006',1.0000)
) v(sku,code,qty)
JOIN products p ON p.sku=v.sku
JOIN materials m ON m.code=v.code;

/* ---------- 9. Inventory batches ---------- */
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,7000,DATEADD(DAY,60,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M001';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,3000,DATEADD(DAY,5,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M001';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,12000,DATEADD(DAY,90,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M002';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,5000,DATEADD(DAY,2,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M003';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,10000,DATEADD(DAY,10,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M003';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,4000,DATEADD(DAY,30,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M004';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,25000,DATEADD(DAY,180,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M005';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,800,NULL,SYSUTCDATETIME() FROM materials WHERE code='M006';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,1500,NULL,SYSUTCDATETIME() FROM materials WHERE code='M007';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,2000,DATEADD(DAY,-3,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M008';
INSERT INTO inventories(material_id,quantity,expiry_date,created_at)
SELECT id,6000,DATEADD(DAY,20,CAST(GETDATE() AS date)),SYSUTCDATETIME() FROM materials WHERE code='M008';

/* ---------- 10. InventoryLog ---------- */
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,10000,'STOCK_IN',NULL,N'今日紅茶葉進貨',SYSUTCDATETIME() FROM materials WHERE code='M001';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-1500,'MANUAL_USE',NULL,N'早班開店備料',SYSUTCDATETIME() FROM materials WHERE code='M001';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-5000,'MANUAL_USE',NULL,N'早班鮮奶備料',SYSUTCDATETIME() FROM materials WHERE code='M003';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-1200,'MANUAL_USE',NULL,N'珍珠煮製領料',SYSUTCDATETIME() FROM materials WHERE code='M004';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-2000,'MANUAL_USE',NULL,N'糖液製作領料',SYSUTCDATETIME() FROM materials WHERE code='M005';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-200,'WASTE',NULL,N'珍珠煮製失敗耗損',SYSUTCDATETIME() FROM materials WHERE code='M004';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-500,'EXPIRED',NULL,N'檸檬汁過期報廢',SYSUTCDATETIME() FROM materials WHERE code='M008';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-1300,'MANUAL_USE',NULL,N'昨日開店備料',DATEADD(DAY,-1,SYSUTCDATETIME()) FROM materials WHERE code='M001';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-4500,'MANUAL_USE',NULL,N'昨日鮮奶領料',DATEADD(DAY,-1,SYSUTCDATETIME()) FROM materials WHERE code='M003';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-1000,'MANUAL_USE',NULL,N'三天前紅茶領料',DATEADD(DAY,-3,SYSUTCDATETIME()) FROM materials WHERE code='M001';
INSERT INTO inventory_logs(material_id,quantity,action,ref_id,note,created_at)
SELECT id,-1200,'MANUAL_USE',NULL,N'十天前紅茶領料',DATEADD(DAY,-10,SYSUTCDATETIME()) FROM materials WHERE code='M001';

/* ---------- 11. Suppliers ---------- */
INSERT INTO suppliers(name,country_calling_code,phone,extension,address,email,status) VALUES
(N'台灣茶葉供應商','+886','0491234567',NULL,N'南投縣名間鄉茶園路100號','tea@supplier.test','ACTIVE'),
(N'每日鮮乳食品','+886','087654321',NULL,N'屏東縣屏東市牧場路20號','milk@supplier.test','ACTIVE'),
(N'包材批發有限公司','+886','073456789','101',N'高雄市前鎮區物流路88號','package@supplier.test','ACTIVE'),
(N'新合作供應商','+886','0223456789',NULL,N'台北市中山區測試路10號','pending@supplier.test','PENDING');

/* ---------- 12. Supplier Notes ---------- */
INSERT INTO supplier_note(supplier_id,remark,created_by_user_id,created_at,updated_at)
SELECT s.id,N'茶葉品質穩定，每月固定叫貨。',u.id,SYSDATETIME(),SYSDATETIME()
FROM suppliers s CROSS JOIN users u WHERE s.email='tea@supplier.test' AND u.username='buyer01';
INSERT INTO supplier_note(supplier_id,remark,created_by_user_id,created_at,updated_at)
SELECT s.id,N'鮮奶需注意冷藏與到貨效期。',u.id,SYSDATETIME(),SYSDATETIME()
FROM suppliers s CROSS JOIN users u WHERE s.email='milk@supplier.test' AND u.username='buyer01';

/* ---------- 13. Purchase Orders ---------- */
INSERT INTO purchase_orders
(supplier_id,status,created_by_user_id,approved_by_user_id,received_by_user_id,total,created_at,updated_at,expected_delivery_date,received_at,receipt_url,decision_remark)
SELECT s.id,'APPROVED',creator.id,approver.id,receiver.id,3200.00,DATEADD(DAY,-5,SYSDATETIME()),DATEADD(DAY,-3,SYSDATETIME()),DATEADD(DAY,-3,CAST(GETDATE() AS date)),DATEADD(DAY,-3,SYSDATETIME()),NULL,N'已核准並完成收貨'
FROM suppliers s
CROSS JOIN (SELECT id FROM users WHERE username='buyer01') creator
CROSS JOIN (SELECT id FROM users WHERE username='manager01') approver
CROSS JOIN (SELECT id FROM users WHERE username='staff01') receiver
WHERE s.email='tea@supplier.test';

INSERT INTO purchase_orders
(supplier_id,status,created_by_user_id,approved_by_user_id,received_by_user_id,total,created_at,updated_at,expected_delivery_date,received_at,receipt_url,decision_remark)
SELECT s.id,'PENDING',creator.id,approver.id,NULL,3000.00,SYSDATETIME(),SYSDATETIME(),DATEADD(DAY,2,CAST(GETDATE() AS date)),NULL,NULL,N'等待主管簽核'
FROM suppliers s
CROSS JOIN (SELECT id FROM users WHERE username='buyer01') creator
CROSS JOIN (SELECT id FROM users WHERE username='manager01') approver
WHERE s.email='milk@supplier.test';

/* ---------- 14. Purchase Order Items ---------- */
INSERT INTO purchase_order_items(purchase_order_id,material_id,quantity,price)
SELECT po.id,m.id,2.0000,800.00 FROM purchase_orders po
JOIN suppliers s ON s.id=po.supplier_id
JOIN materials m ON m.code='M001'
WHERE s.email='tea@supplier.test' AND po.status='APPROVED';
INSERT INTO purchase_order_items(purchase_order_id,material_id,quantity,price)
SELECT po.id,m.id,2.0000,750.00 FROM purchase_orders po
JOIN suppliers s ON s.id=po.supplier_id
JOIN materials m ON m.code='M002'
WHERE s.email='tea@supplier.test' AND po.status='APPROVED';
INSERT INTO purchase_order_items(purchase_order_id,material_id,quantity,price)
SELECT po.id,m.id,20.0000,150.00 FROM purchase_orders po
JOIN suppliers s ON s.id=po.supplier_id
JOIN materials m ON m.code='M003'
WHERE s.email='milk@supplier.test' AND po.status='PENDING';

/* ---------- 15. Sales Orders ---------- */
INSERT INTO sales_orders(order_number,status,payment_method,total_amount,created_by_user_id,voided_by_user_id,created_at,voided_at,note,void_reason)
SELECT CONCAT(CONVERT(char(8),GETDATE(),112),'-001'),'COMPLETED','CASH',185.00,id,NULL,SYSDATETIME(),NULL,N'門市現金交易',NULL
FROM users WHERE username='staff01';
INSERT INTO sales_orders(order_number,status,payment_method,total_amount,created_by_user_id,voided_by_user_id,created_at,voided_at,note,void_reason)
SELECT CONCAT(CONVERT(char(8),DATEADD(DAY,-1,GETDATE()),112),'-001'),'COMPLETED','MOBILE_PAYMENT',150.00,id,NULL,DATEADD(DAY,-1,SYSDATETIME()),NULL,N'昨日行動支付交易',NULL
FROM users WHERE username='staff01';
INSERT INTO sales_orders(order_number,status,payment_method,total_amount,created_by_user_id,voided_by_user_id,created_at,voided_at,note,void_reason)
SELECT CONCAT(CONVERT(char(8),GETDATE(),112),'-VOID01'),'VOIDED','CREDIT_CARD',65.00,staff.id,manager.id,DATEADD(HOUR,-2,SYSDATETIME()),DATEADD(HOUR,-1,SYSDATETIME()),N'測試作廢交易',N'顧客取消訂單'
FROM (SELECT id FROM users WHERE username='staff01') staff
CROSS JOIN (SELECT id FROM users WHERE username='manager01') manager;

/* ---------- 16. Sales Order Items ---------- */
INSERT INTO sales_order_items(sales_order_id,product_id,product_sku,product_name,quantity,unit_price,subtotal)
SELECT so.id,p.id,p.sku,p.name,1,p.selling_price,p.selling_price FROM sales_orders so JOIN products p ON p.sku='P005' WHERE so.order_number=CONCAT(CONVERT(char(8),GETDATE(),112),'-001');
INSERT INTO sales_order_items(sales_order_id,product_id,product_sku,product_name,quantity,unit_price,subtotal)
SELECT so.id,p.id,p.sku,p.name,2,p.selling_price,p.selling_price*2 FROM sales_orders so JOIN products p ON p.sku='P006' WHERE so.order_number=CONCAT(CONVERT(char(8),GETDATE(),112),'-001');
INSERT INTO sales_order_items(sales_order_id,product_id,product_sku,product_name,quantity,unit_price,subtotal)
SELECT so.id,p.id,p.sku,p.name,2,p.selling_price,p.selling_price*2 FROM sales_orders so JOIN products p ON p.sku='P005' WHERE so.order_number=CONCAT(CONVERT(char(8),DATEADD(DAY,-1,GETDATE()),112),'-001');
INSERT INTO sales_order_items(sales_order_id,product_id,product_sku,product_name,quantity,unit_price,subtotal)
SELECT so.id,p.id,p.sku,p.name,1,p.selling_price,p.selling_price FROM sales_orders so JOIN products p ON p.sku='P004' WHERE so.status='VOIDED';

/* ---------- 17. Leave Requests ---------- */
INSERT INTO leave_requests(applicant_id,leave_type,start_date,end_date,reason,status,created_at)
SELECT id,'ANNUAL',DATEADD(DAY,7,CAST(GETDATE() AS date)),DATEADD(DAY,8,CAST(GETDATE() AS date)),N'家庭旅遊','PENDING',SYSUTCDATETIME()
FROM users WHERE username='staff01';
INSERT INTO leave_requests(applicant_id,leave_type,start_date,end_date,reason,status,created_at)
SELECT id,'SICK',DATEADD(DAY,-5,CAST(GETDATE() AS date)),DATEADD(DAY,-5,CAST(GETDATE() AS date)),N'身體不適請病假','APPROVED',DATEADD(DAY,-6,SYSUTCDATETIME())
FROM users WHERE username='buyer01';

/* ---------- 18. Workflows ---------- */
INSERT INTO workflows(document_type,document_id,status,applicant_id,approver_id,created_at)
SELECT 'LEAVE',lr.id,'PENDING',lr.applicant_id,u.id,SYSUTCDATETIME()
FROM leave_requests lr CROSS JOIN users u
WHERE lr.status='PENDING' AND u.username='manager01';
INSERT INTO workflows(document_type,document_id,status,applicant_id,approver_id,created_at)
SELECT 'ORDER',po.id,po.status,po.created_by_user_id,po.approved_by_user_id,DATEADD(MINUTE,1,po.created_at)
FROM purchase_orders po;

/* ---------- 19. Workflow Logs ---------- */
INSERT INTO workflow_logs(workflow_id,action,operator_id,remark,created_at)
SELECT w.id,'SUBMIT',w.applicant_id,N'送出簽核申請',w.created_at FROM workflows w;
INSERT INTO workflow_logs(workflow_id,action,operator_id,remark,created_at)
SELECT w.id,'APPROVE',w.approver_id,N'主管核准',DATEADD(HOUR,1,w.created_at)
FROM workflows w WHERE w.status='APPROVED';

/* ---------- 20. Notifications ---------- */
INSERT INTO notification_record(user_id,title,content,category,type,action_route,is_read,created_at)
SELECT id,N'庫存偏低',N'黑糖珍珠目前低於安全庫存，請確認是否需要補貨。','inventory','warning','/inventory',0,SYSUTCDATETIME()
FROM users WHERE username='manager01';
INSERT INTO notification_record(user_id,title,content,category,type,action_route,is_read,created_at)
SELECT id,N'待簽核請假單',N'目前有新的請假申請等待處理。','workflow','info','/leave',0,SYSUTCDATETIME()
FROM users WHERE username='manager01';
INSERT INTO notification_record(user_id,title,content,category,type,action_route,is_read,created_at)
SELECT id,N'採購單已建立',N'鮮奶採購單目前等待簽核。','workflow','success','/purchase-orders',1,SYSUTCDATETIME()
FROM users WHERE username='buyer01';

PRINT N'飲料店 ERP 完整開發假資料初始化完成';
