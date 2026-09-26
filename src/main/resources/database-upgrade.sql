/*
 * 銷售與庫存同步功能升級程序
 * SQL Server / SSMS 可直接執行，且可安全重複執行。
 */
CREATE OR ALTER PROCEDURE dbo.usp_UpgradeSalesInventorySync
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        IF OBJECT_ID(N'dbo.inventory_logs', N'U') IS NULL
        BEGIN
            THROW 50001, N'找不到 dbo.inventory_logs 資料表。', 1;
        END;

        IF OBJECT_ID(N'dbo.system_settings', N'U') IS NULL
        BEGIN
            THROW 50002, N'找不到 dbo.system_settings 資料表。', 1;
        END;

        /* 報廢銷售單時，用來精確回補當初實際扣除的庫存批次。 */
        IF COL_LENGTH(N'dbo.inventory_logs', N'inventory_batch_id') IS NULL
        BEGIN
            EXEC sys.sp_executesql N'
                ALTER TABLE dbo.inventory_logs
                    ADD inventory_batch_id BIGINT NULL;
            ';
        END;

        /* 預設關閉，確保未啟用時仍維持原本銷售流程。 */
        IF NOT EXISTS
        (
            SELECT 1
            FROM dbo.system_settings
            WHERE setting_key = 'SALES_INVENTORY_SYNC_ENABLED'
        )
        BEGIN
            INSERT INTO dbo.system_settings
                (setting_key, setting_value, description, updated_at, updated_by_user_id)
            VALUES
                ('SALES_INVENTORY_SYNC_ENABLED', 'false',
                 N'是否在銷售完成時依 BOM 同步扣除庫存',
                 SYSDATETIME(), NULL);
        END;

        COMMIT TRANSACTION;

        SELECT
            CAST(1 AS bit) AS success,
            N'銷售與庫存同步功能資料庫升級完成。' AS message,
            COL_LENGTH(N'dbo.inventory_logs', N'inventory_batch_id') AS inventory_batch_id_length,
            (
                SELECT setting_value
                FROM dbo.system_settings
                WHERE setting_key = 'SALES_INVENTORY_SYNC_ENABLED'
            ) AS sales_inventory_sync_enabled;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
        BEGIN
            ROLLBACK TRANSACTION;
        END;

        THROW;
    END CATCH;
END;
GO

/* 建立或更新程序後立即執行一次。 */
EXEC dbo.usp_UpgradeSalesInventorySync;
GO
