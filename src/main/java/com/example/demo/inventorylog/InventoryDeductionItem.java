package com.example.demo.inventorylog;

import java.math.BigDecimal;

/**
 * 一筆待換算 BOM 用量的銷售商品。
 */
public record InventoryDeductionItem(Long productId, BigDecimal quantity) {
}
