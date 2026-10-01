/* =============================================================
   大量交易與分析查詢使用的索引
   可重複執行；若索引已存在不會再次建立。
   ============================================================= */
SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_sales_orders_status_created_at' AND object_id = OBJECT_ID('dbo.sales_orders'))
    CREATE INDEX IX_sales_orders_status_created_at
        ON dbo.sales_orders(status, created_at)
        INCLUDE (total_amount, payment_method, created_by_user_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_sales_order_items_sales_order_id' AND object_id = OBJECT_ID('dbo.sales_order_items'))
    CREATE INDEX IX_sales_order_items_sales_order_id
        ON dbo.sales_order_items(sales_order_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_sales_order_items_product_id' AND object_id = OBJECT_ID('dbo.sales_order_items'))
    CREATE INDEX IX_sales_order_items_product_id
        ON dbo.sales_order_items(product_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_purchase_orders_status_delivery' AND object_id = OBJECT_ID('dbo.purchase_orders'))
    CREATE INDEX IX_purchase_orders_status_delivery
        ON dbo.purchase_orders(status, expected_delivery_date, created_at);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_purchase_order_items_order_material' AND object_id = OBJECT_ID('dbo.purchase_order_items'))
    CREATE INDEX IX_purchase_order_items_order_material
        ON dbo.purchase_order_items(purchase_order_id, material_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_inventory_logs_action_created_at' AND object_id = OBJECT_ID('dbo.inventory_logs'))
    CREATE INDEX IX_inventory_logs_action_created_at
        ON dbo.inventory_logs(action, created_at)
        INCLUDE (material_id, quantity, ref_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_inventories_material_expiry' AND object_id = OBJECT_ID('dbo.inventories'))
    CREATE INDEX IX_inventories_material_expiry
        ON dbo.inventories(material_id, expiry_date)
        INCLUDE (quantity);
GO
