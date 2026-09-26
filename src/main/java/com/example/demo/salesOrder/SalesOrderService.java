package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.documentnumber.DocumentNumberService;
import com.example.demo.documentnumber.DocumentNumberType;
import com.example.demo.inventorylog.InventoryDeductionItem;
import com.example.demo.inventorylog.InventoryLogService;
import com.example.demo.products.ProductRepository;
import com.example.demo.products.Products;
import com.example.demo.products.ProductType;
import com.example.demo.systemsetting.SystemSettingKey;
import com.example.demo.systemsetting.SystemSettingService;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;
import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrderService {
	
	private final SalesOrderRepository salesOrderRepo;
	private final SalesOrderItemRepository salesOrderItemRepo;
	private final UsersRepository userRepo;
	private final ProductRepository proRepo;
	private final InventoryLogService inventoryLogService;
	private final SystemSettingService systemSettingService;
	private final BomRepository bomRepository;
	
	private final DocumentNumberService documentNumberService;
	
	
	//產生銷售單號
	public String generateOrderNumber() {
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		
		long count = salesOrderRepo.countByOrderNumberStartingWith(date);
                
		long sequence = count + 1;

		return date + "-" + String.format("%03d", sequence);

     	}

	
	
	//---新增---
	@Transactional
	public SalesOrderRespoDTO createSalesOrder(SalesOrderCreDTO salesOrderCreDTO
		,Long loginUserId
		){
	//找登入者
	User loginUser = userRepo.findById(loginUserId)
            .orElseThrow(() ->
                    new RuntimeException("找不到登入使用者"));
	//建銷售單
	SalesOrders salesOrder = new SalesOrders();
	//set付款方式
	salesOrder.setPaymentMethod(salesOrderCreDTO.getPaymentMethod());
	//setNote
	salesOrder.setNote(salesOrderCreDTO.getNote());
	//set人
	salesOrder.setCreatedBy(loginUser);

    // 【本次新增：ECPay 測試金流】
    // 現金維持原本「建立即完成」流程；信用卡與行動支付必須先等待 ECPay 確認。
    boolean requiresEcpay = salesOrderCreDTO.getPaymentMethod() == PaymentMethod.CREDIT_CARD
            || salesOrderCreDTO.getPaymentMethod() == PaymentMethod.MOBILE_PAYMENT;
    salesOrder.setStatus(requiresEcpay
            ? SalesOrderStatus.PENDING_PAYMENT
            : SalesOrderStatus.COMPLETED);
	
	//總金額
	BigDecimal totalAmount = BigDecimal.ZERO;
	
	Set<Long> productIds = new HashSet<>();

	for (int i = 0; i < salesOrderCreDTO.getItems().size(); i++) {

	    SalesOrderItemCreDTO itemDTO =
	            salesOrderCreDTO.getItems().get(i);

	    Long productId = itemDTO.getProductId();

	    //檢查商品是否重複
	    if (productIds.contains(productId)) {
	        throw new RuntimeException(
	                "商品 ID：" + productId + " 重複出現"
	        );
	    }

	    // 沒重複就記錄起來
	    productIds.add(productId);
	    
        // 找商品
        Products product = proRepo.findById(itemDTO.getProductId())
        		.orElseThrow(() ->new RuntimeException("找不到商品"));
        
        //【我新增】停用商品不可再建立新銷售明細。
        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new IllegalStateException("商品「" + product.getName() + "」目前不是可銷售狀態");
        }

        // 商品目前售價
        BigDecimal unitPrice = product.getSellingPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("商品「" + product.getName() + "」尚未設定有效售價");
        }
        
        // 數量
        BigDecimal quantity = itemDTO.getQuantity();
                
        // 小計 = 單價 × 數量
        BigDecimal subtotal = unitPrice.multiply(quantity);
               
        // 加到總額
        totalAmount = totalAmount.add(subtotal);
                
        // 4. 建立銷售明細
        SalesOrderItem item =  new SalesOrderItem();
               
        item.setSalesOrder(salesOrder);

        item.setProduct(product);

        item.setQuantity(quantity);

        // 記錄結帳當下的商品資料
        item.setProductSku(product.getSku());

        item.setProductName(product.getName());

            item.setUnitPrice(unitPrice);

            //subtotal 在 SalesOrderItem Entity 中不可為 null，必須存入本次計算的小計。
            item.setSubtotal(subtotal);

            // 加入主單
            salesOrder.getItems().add(item);
    }

	validateLatestBom(salesOrder.getItems());


    // 5. 設定後端計算完成的總額
    salesOrder.setTotalAmount(totalAmount);

        // 【本次修改：共用取號模組】
        // 商品驗證完成後才取得正式銷售單號，縮短鎖定時間並避免同時結帳撞號。
        salesOrder.setOrderNumber(
                documentNumberService.nextNumber(DocumentNumberType.SALES_ORDER));
    
        // 6. 儲存銷售單主檔
        SalesOrders savedOrder =
                salesOrderRepo.save(salesOrder);

        //SalesOrders.items 沒有 Cascade，主檔 save 不會自動儲存明細。
        // 因此先讓主檔取得 id，再逐筆儲存每一筆 SalesOrderItem。
        for (SalesOrderItem item : salesOrder.getItems()) {
            salesOrderItemRepo.save(item);
        }

        // 【本次修改：ECPay 測試金流】
        // 現金訂單仍在建立時同步扣庫存；電子支付要等 ECPay 驗證付款成功後才扣庫存。
        if (savedOrder.getStatus() == SalesOrderStatus.COMPLETED
                && systemSettingService.isEnabled(SystemSettingKey.SALES_INVENTORY_SYNC_ENABLED)) {
            List<InventoryDeductionItem> deductionItems = salesOrder.getItems().stream()
                    .map(item -> new InventoryDeductionItem(
                            item.getProduct().getId(), item.getQuantity()))
                    .toList();

            inventoryLogService.deductSale(deductionItems, savedOrder.getId());
        }

        return  SalesOrderRespoDTO.fromEntity(savedOrder);
    }	

	private void validateLatestBom(List<SalesOrderItem> items) {
		for (SalesOrderItem item : items) {
			Products product = item.getProduct();
			if (product.getProductType() == ProductType.RETAIL) {
				continue;
			}

			List<Bom> latestBom = bomRepository.findByProductId(product.getId());
			if (latestBom.isEmpty()) {
				throw new IllegalStateException("商品「" + product.getName() + "」沒有 BOM，無法完成交易");
			}
			for (Bom component : latestBom) {
				if (!"ACTIVE".equalsIgnoreCase(component.getMaterial().getStatus())) {
					throw new IllegalStateException("商品「" + product.getName()
							+ "」的 BOM 含停用原物料「" + component.getMaterial().getName() + "」，無法完成交易");
				}
			}
		}
	}

    // 【本次新增：ECPay 測試金流】
    // ECPay 的 ReturnURL 與 OrderResultURL 可能重複通知，因此使用資料庫鎖與狀態判斷確保只扣一次庫存。
    @Transactional
    public SalesOrderRespoDTO completeEcpayPayment(Long salesOrderId, BigDecimal paidAmount) {
        SalesOrders salesOrder = salesOrderRepo.findByIdWithLock(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("找不到 ECPay 對應的銷售單"));

        if (salesOrder.getStatus() == SalesOrderStatus.COMPLETED) {
            return SalesOrderRespoDTO.fromEntity(salesOrder);
        }

        if (salesOrder.getStatus() != SalesOrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("此銷售單目前不可完成 ECPay 付款");
        }

        if (paidAmount == null || salesOrder.getTotalAmount().compareTo(paidAmount) != 0) {
            throw new IllegalArgumentException("ECPay 回傳金額與銷售單金額不一致");
        }

        if (systemSettingService.isEnabled(SystemSettingKey.SALES_INVENTORY_SYNC_ENABLED)) {
            List<InventoryDeductionItem> deductionItems = salesOrder.getItems().stream()
                    .map(item -> new InventoryDeductionItem(
                            item.getProduct().getId(), item.getQuantity()))
                    .toList();

            inventoryLogService.deductSale(deductionItems, salesOrder.getId());
        }

        salesOrder.setStatus(SalesOrderStatus.COMPLETED);
        return SalesOrderRespoDTO.fromEntity(salesOrderRepo.save(salesOrder));
    }

	//---報廢---
	@Transactional
	public SalesOrderRespoDTO voidSalesOrder(
		Long salesOrderId,
		Long loginUserId,
		String voidReason
		) {
	SalesOrders salesOrder = salesOrderRepo.findByIdWithLock(salesOrderId).orElseThrow(() ->
    								new RuntimeException("找不到銷售單"));
	
	if(salesOrder.getStatus()== SalesOrderStatus.VOIDED) {
		throw new RuntimeException("此銷售單已經作廢");
	}

    // 【本次新增：ECPay 測試金流】待付款訂單尚未扣庫存，不允許走「回補庫存」的作廢流程。
    if (salesOrder.getStatus() == SalesOrderStatus.PENDING_PAYMENT) {
        throw new IllegalStateException("此銷售單仍在等待 ECPay 付款結果，暫時不可作廢");
    }
	
	User loginUser = userRepo.findById(loginUserId)
            .orElseThrow(() ->
                    new RuntimeException("找不到登入使用者"));

	// 若建立銷售單時曾同步扣庫存，報廢時依實際扣除紀錄精確回補。
	// 沒有扣庫存紀錄表示當時開關未啟用，維持既有報廢流程。
	inventoryLogService.restoreSale(salesOrderId);
	
	salesOrder.setStatus(SalesOrderStatus.VOIDED);
	salesOrder.setVoidedBy(loginUser);
	salesOrder.setVoidReason(voidReason);
	salesOrder.setVoidedAt(LocalDateTime.now());
	SalesOrders savedOrder = salesOrderRepo.save(salesOrder);
	
	return SalesOrderRespoDTO.fromEntity(savedOrder);
}

	//---查詢---


	//單筆
	//明細頁會讀取 items、product 與 createdBy 等 LAZY 關聯。
	@Transactional(readOnly = true)
	public SalesOrderDetailRespoDTO findById(Long id) {

    SalesOrders order = salesOrderRepo.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("找不到銷售單"));

    return SalesOrderDetailRespoDTO.fromEntity(order);
}
	//分頁
	@Transactional(readOnly = true)
	public Page<SalesOrderListRespoDTO> findSalesOrderPage(
	        String keyword,
	        SalesOrderStatus status,
	        PaymentMethod paymentMethod,
	        Long createdById,
	        LocalDate startDate,
	        LocalDate endDate,
	        BigDecimal minAmount,
	        BigDecimal maxAmount,
	        int page,
	        int size) {
	
	    // 限制每頁只能 10 / 30 / 50 筆
	    if (size != 5
	            && size != 10
	            && size != 30
	            && size != 50) {
	        size = 10;
	    }
	
	    //  不可以是負數
	    if (page < 0) {
	        page = 0;
	    }
	    
	    // 日期區間檢查
	    if (startDate != null
	            && endDate != null
	            && startDate.isAfter(endDate)) {
	        throw new IllegalArgumentException("開始日期不能晚於結束日期");
	    }
	
	    // 金額區間檢查
	    if (minAmount != null
	            && minAmount.compareTo(BigDecimal.ZERO) < 0) {
	        throw new IllegalArgumentException("最低金額不能小於 0");
	    }
	
	    if (maxAmount != null
	            && maxAmount.compareTo(BigDecimal.ZERO) < 0) {
	
	        throw new IllegalArgumentException("最高金額不能小於 0");
	    }
	
	    // minAmount > maxAmount
	    if (minAmount != null
	            && maxAmount != null
	            && minAmount.compareTo(maxAmount) > 0) {
	
	        throw new IllegalArgumentException("最低金額不能大於最高金額");
	    }
	
	    // 日期轉換
	    // LocalDate → LocalDateTime
	    LocalDateTime startDateTime = null;
	    LocalDateTime endDateTime = null;
	
	    if (startDate != null) {
	        startDateTime = startDate.atStartOfDay();
	    }
	
	    if (endDate != null) {
	
	    	// endDate = 2026-09-30 = createdAt < 2026-10-01 00:00
			//所以 9/30 一整天都會被包含
	        endDateTime = endDate
	                        .plusDays(1)
	                        .atStartOfDay();
	    }
	
	    // 處理搜尋關鍵字
	    String searchKeyword = null;
	
	    if (keyword != null
	            && !keyword.trim().isEmpty()) {
	        searchKeyword =
	                keyword.trim();
	    }
	
	            // 金額使用數值比較，480 與 480.00 視為相同金額。
        BigDecimal keywordAmount = null;
        if (searchKeyword != null && searchKeyword.matches("[0-9]+(?:\\.[0-9]{1,2})?")) {
            try {
                BigDecimal parsedAmount = new BigDecimal(searchKeyword);
                // totalAmount 為 decimal(18,2)，避免超出資料庫金額範圍。
                if (parsedAmount.compareTo(new BigDecimal("9999999999999999.99")) <= 0) {
                    keywordAmount = parsedAmount;
                }
            } catch (NumberFormatException ignored) {
                // 非有效金額仍可使用文字欄位搜尋。
            }
        }
        // 建立分頁
	    Pageable pageable =
	            PageRequest.of(
	                    page,
	                    size,
	                    // 最新的銷售單排最前面
	                    Sort.by( Sort.Direction.DESC,
	                            "createdAt")
	            );
	
	    //Repository 查詢
	    Page<SalesOrders> salesOrderPage = salesOrderRepo.searchSalesOrders(
	                    status,
	                    paymentMethod,
	                    createdById,
	                    startDateTime,
	                    endDateTime,
	                    minAmount,
	                    maxAmount,
	                    searchKeyword,
                    keywordAmount,
	                    pageable);
	
	    // Entity → DTO
	    List<SalesOrderListRespoDTO> dtoList = new ArrayList<>();
	
	    for (SalesOrders salesOrder
	            : salesOrderPage.getContent()) {
	
	        SalesOrderListRespoDTO dto = SalesOrderListRespoDTO
	                        .fromEntity(salesOrder);
	        dtoList.add(dto);
	    }
	    //Page<DTO>
	    return new PageImpl<SalesOrderListRespoDTO>(
	            dtoList,
	            pageable,
	            salesOrderPage.getTotalElements()
	    );
	}
	
//	// 全部查詢
//	public List<SalesOrderRespoDTO> findAllSalesOrders()
//
//	// 單號查詢
//	public SalesOrderRespoDTO findByOrderNumber(String orderNumber)
//
//	// 狀態查詢
//	public List<SalesOrderRespoDTO> findByStatus(SalesOrderStatus status)
//
//	// 日期區間查詢
//	public List<SalesOrderRespoDTO> findByCreatedAtBetween(
//	        LocalDateTime start,
//	        LocalDateTime end)
//	 // 分頁
//	 // 新增扣庫存
//	 // 報廢回補庫存
//	 // 處理訂單號併發

}


