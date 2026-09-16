package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.materials.MaterialRepository;
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

        // 7. 根據 materialId 查詢原物料
        Material material = materialRepository
                .findById(itemDTO.getMaterialId())
                .orElseThrow(() -> new RuntimeException("找不到原物料"));

        // 8. 建立明細 Entity
        PurchaseOrderItems item = new PurchaseOrderItems();

        // 設定多對一關聯：這筆明細屬於哪張採購單
        item.setPurchaseOrder(purchaseOrder);

        // 設定這筆明細採購的原物料
        item.setMaterial(material);
    
    
    return PurchaseOrderResponseDTO.toResponseDTO(saved);
    
    private String createTemporaryOrderNumber() {
        return "TMP-" + System.currentTimeMillis(); //目前時間的毫秒數
    }
}
    // ---查詢---
    // 單筆
    public PurchaseOrders findPurchaseOrderById(Long id) {
        return purchaseOrdersRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單"));
    }
    // 多筆
    public List<PurchaseOrders> findPurchaseOrdersByIds(
        List<Long> ids) {

    return purchaseOrdersRepo.findAllById(ids);
    }
    // 全部
    public List<PurchaseOrders> findAllPurchaseOrders() {

    return purchaseOrdersRepo.findAll();
    }
  
    // 修改
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

    // 刪除
    // 刪除單筆
    public void deletePurchaseOrder(Long id) {
        if (!purchaseOrdersRepo.existsById(id)) {
            throw new IllegalArgumentException("找不到採購單");
        }
        purchaseOrdersRepo.deleteById(id);
    }
    //多筆

    // 採購流程
   
    // 送出審核

    // 核准採購單

    // 駁回採購單

    // 完成採購單

}