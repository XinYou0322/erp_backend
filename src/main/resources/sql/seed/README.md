# 開發資料腳本

## 一般啟動

一般啟動使用 `ddl-auto=update` 且不會自動執行假資料，既有資料不會因重啟而被清除。

## 重建基礎資料

需要清空並重建小量開發資料時，使用 Spring profile：

```text
--spring.profiles.active=reset
```

此模式會執行 `data.sql` 與 `indexes.sql`，但不會建立大量歷史交易。

## 增加歷史分析資料

1. 先完成基礎資料建立。
2. 使用 SQL Server Management Studio 開啟 `history-data.sql`。
3. 修改檔案最上方的 `@StartMonth`、`@EndMonth`、`@OrdersPerMonth` 與 `@SeedBatch`。
4. 執行腳本。腳本每個月份個別提交，且相同月份與單號可安全重跑。

大量歷史資料不應加入 Spring Boot 的自動初始化位置。

## 索引

既有資料庫可手動執行 `indexes.sql`。使用 reset profile 時會自動建立索引。
