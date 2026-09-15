package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.demo.products.ProductRepository;
import com.example.demo.products.Products;
import com.example.demo.suppliers.SuppliersRepository;
import com.example.demo.suppliersNotes.SuppliersNotesRepository;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrderService {
	
	private final SalesOrderRepository salesOrderRepo;
	private final UsersRepository userRepo;
	private final ProductRepository proRepo;
	
	
	
	//產生銷售單號
	public String generateOrderNumber() {
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		
		long count = salesOrderRepo.countByOrderNumberStartingWith(date);
                
		long sequence = count + 1;

		return date + "-" + String.format("%03d", sequence);

     	}
	
	//---新增---
	public SalesOrderRespoDTO createSalesOrder(SalesOrderCreDTO salesOrderCreDTO
			,Long loginUserId
			){
		//找登入者
		User loginUser = userRepo.findById(loginUserId)
                .orElseThrow(() ->
                        new RuntimeException("找不到登入使用者"));
		//建銷售單
		SalesOrders salesOrder = new SalesOrders();
		//set單號
		salesOrder.setOrderNumber(generateOrderNumber());
		//set付款方式
		salesOrder.setPaymentMethod(salesOrderCreDTO.getPaymentMethod());
		//setNote
		salesOrder.setNote(salesOrderCreDTO.getNote());
		//set人
		salesOrder.setCreatedBy(loginUser);
		
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
                    
            // 商品目前售價
            BigDecimal unitPrice = product.getSellingPrice();

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

            // 加入主單
            salesOrder.getItems().add(item);
        }

        // 5. 設定後端計算完成的總額
        salesOrder.setTotalAmount(totalAmount);
        
        // 6. 儲存
        SalesOrders savedOrder =
                salesOrderRepo.save(salesOrder);

        return  SalesOrderRespoDTO.fromEntity(savedOrder);
    }	
	//---報廢---
	public SalesOrderRespoDTO voidSalesOrder(
			Long salesOrderId,
			Long loginUserId,
			String voidReason
			) {
		SalesOrders salesOrder = salesOrderRepo.findById(salesOrderId).orElseThrow(() ->
        								new RuntimeException("找不到銷售單"));
		
		if(salesOrder.getStatus()== SalesOrderStatus.VOIDED) {
			throw new RuntimeException("此銷售單已經作廢");
		}
		
		User loginUser = userRepo.findById(loginUserId)
                .orElseThrow(() ->
                        new RuntimeException("找不到登入使用者"));
		
		salesOrder.setStatus(SalesOrderStatus.VOIDED);
		salesOrder.setVoidedBy(loginUser);
		salesOrder.setVoidReason(voidReason);
		salesOrder.setVoidedAt(LocalDateTime.now());
		SalesOrders savedOrder = salesOrderRepo.save(salesOrder);
		
		return SalesOrderRespoDTO.fromEntity(savedOrder);
	}

	//---查詢---
	public SalesOrderRespoDTO findSalesOrderById(Long salesOrderId) {
		
		SalesOrders salesOrder = salesOrderRepo.findById(salesOrderId).orElseThrow(() ->
		new RuntimeException("找不到銷售單"));
		
		return SalesOrderRespoDTO.fromEntity(salesOrder);
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
