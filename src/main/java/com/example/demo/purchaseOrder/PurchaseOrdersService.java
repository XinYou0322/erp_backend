package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.purchaseOrderItem.PurchaseOrderItems;
import com.example.demo.purchaseOrderItem.PurchaseOrderItemsCreDTO;
import com.example.demo.suppliers.Suppliers;
import com.example.demo.suppliers.SuppliersRepository;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor
public class PurchaseOrdersService {
    private final PurchaseOrdersRepository purchaseOrdersRepo;
    private final SuppliersRepository suppliersRepo;
    private final MaterialRepository materialRepo;
    private final UsersRepository usersRepo;
    
    //---新增---
    // 新增一張採購單
    @Transactional
    public PurchaseOrderResponseDTO insertPurchaseOrder(PurchaseOrderCreateDTO dto,Long loginUserId) {
    // supplierId → Supplier Entity
    Suppliers supplier = suppliersRepo.findById(dto.getSupplierId())
        .orElseThrow(() -> new IllegalArgumentException("找不到此供應商"));

    User creator = usersRepo.findById(loginUserId)   
            .orElseThrow(() -> new RuntimeException("找不到登入者資料"));
    
    User approver = usersRepo.findById(dto.getApprovedByUserId())
    		.orElseThrow(() -> new RuntimeException("找不到簽核人資料"));
    
    PurchaseOrders purchaseOrder = new PurchaseOrders();
    purchaseOrder.setOrderNumber(createTemporaryOrderNumber());
    purchaseOrder.setSupplier(supplier);
    purchaseOrder.setCreatedBy(creator);
    purchaseOrder.setApprovedBy(approver);
    purchaseOrder.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
    purchaseOrder.setStatus(PurchaseOrdersStatus.DRAFT);//草稿
    
    BigDecimal totalAmount = BigDecimal.ZERO;
    
    //處理明細 
    for (int i = 0; i < dto.getItems().size(); i++) {

        PurchaseOrderItemsCreDTO itemDTO =
                dto.getItems().get(i);

        //查詢原物料
        Material material = materialRepo
                .findById(itemDTO.getMaterialId())
                .orElseThrow(() -> new RuntimeException("找不到原物料"));

        // 
        PurchaseOrderItems item = new PurchaseOrderItems();
        //多對一關聯：這筆明細屬於哪張採購單
        item.setPurchaseOrder(purchaseOrder);
        item.setMaterial(material);
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getPrice());
        // 小計 = 數量 乘(multiply) 單價     
        BigDecimal subtotal =
                itemDTO.getQuantity().multiply(itemDTO.getPrice());

        //總金額
        totalAmount = totalAmount.add(subtotal);

        //將明細加入主單的明細集合
        purchaseOrder.getItems().add(item);
    	}
    
    	//設定後端計算出的總金額
    	purchaseOrder.setTotal(totalAmount);

    	// 主單及所有明細
    	PurchaseOrders savedOrder =
            purchaseOrdersRepo.save(purchaseOrder);

    	//Entity -> DTO
    	return PurchaseOrderResponseDTO.fromEntity(savedOrder);
}
    
    //單號 用時間
    private String createTemporaryOrderNumber() {
        return "TMP-" + System.currentTimeMillis(); //目前時間的毫秒數
    }

    // ---查詢---
    // 單筆
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDTO findPurchaseOrderById(Long id) {
    	PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單，採購單 ID：" + id));
         return PurchaseOrderResponseDTO.fromEntity(purchaseOrder);
    }
    // 多筆
    @Transactional(readOnly = true)
    public List<PurchaseOrderQueryResponseDTO> findPurchaseOrdersByIds(
        List<Long> ids) {
    	List<PurchaseOrders> purchaseOrders = purchaseOrdersRepo.findAllById(ids);
    	List<Long> foundIds = new ArrayList<>();
    	for (PurchaseOrders purchaseOrder: purchaseOrders) {
            foundIds.add(purchaseOrder.getId());
        }
    	List<Long> notFoundIds = new ArrayList<>();
        for (Long id : ids) {
        if (!foundIds.contains(id)) {
            notFoundIds.add(id);
        }
        }
        
        
        //將每筆 Entity -> ResponseDTO
        List<PurchaseOrderResponseDTO> responseDTOList = new ArrayList<>();
        
        for (PurchaseOrders purchaseOrder : purchaseOrders) {

            PurchaseOrderResponseDTO responseDTO = PurchaseOrderResponseDTO.fromEntity(purchaseOrder);
                   
            responseDTOList.add(responseDTO);
        }    
        // 建立最後回傳結果
        PurchaseOrderQueryResponseDTO result = new PurchaseOrderQueryResponseDTO();           

        // 查到的資料
        result.setPurchaseOrders(responseDTOList);

        // 沒查到的 ID
        result.setNotFoundIds(notFoundIds);

        //設定提示訊息
        if (notFoundIds.isEmpty()) {

            result.setMessage("全部採購單查詢成功");

        } else {

            result.setMessage(
                    "以下採購單查無此資料：" + notFoundIds
            );
        }


        // 同時回傳「有的資料 + 沒有的資料」
        return result;
    }
    // 全部
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponseDTO> findAllPurchaseOrders() {
    	List<PurchaseOrders> purchaseOrders = purchaseOrdersRepo.findAll();
    	List<PurchaseOrderResponseDTO> responseDTOList =  new ArrayList<>();
    	for (PurchaseOrders purchaseOrder : purchaseOrders) {

            PurchaseOrderResponseDTO responseDTO = PurchaseOrderResponseDTO.fromEntity(purchaseOrder);
                   
            responseDTOList.add(responseDTO);
        }        
    	
    return responseDTOList;
    }
  
    // ---修改---
    
    public PurchaseOrders updatePurchaseOrder(Long id, PurchaseOrders newPurchaseOrder) {
        // 先確認這筆採購單存不存在
        PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單"));

        // 修改資料
        purchaseOrder.setSupplier(newPurchaseOrder.getSupplier());
        purchaseOrder.setCreatedBy(newPurchaseOrder.getCreatedBy());
        purchaseOrder.setExpectedDeliveryDate(newPurchaseOrder.getExpectedDeliveryDate());
        purchaseOrder.setStatus(newPurchaseOrder.getStatus());
        purchaseOrder.setTotal(newPurchaseOrder.getTotal());

        // 存回資料庫
        return purchaseOrdersRepo.save(purchaseOrder);
    }

//    // 刪除
//    // 刪除單筆
//    public void deletePurchaseOrder(Long id) {
//        if (!purchaseOrdersRepo.existsById(id)) {
//            throw new IllegalArgumentException("找不到採購單");
//        }
//        purchaseOrdersRepo.deleteById(id);
//    }


    // 採購流程
   
    // 送出審核

    // 核准採購單

    // 駁回採購單

    // 完成採購單

}