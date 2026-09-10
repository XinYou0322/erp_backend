package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;


import org.springframework.stereotype.Service;

import com.example.demo.suppliers.Suppliers;
import com.example.demo.suppliers.SuppliersRepository;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor
public class PurchaseOrdersService {
    private final PurchaseOrdersRepository purchaseOrdersRepo;
    private final SuppliersRepository suppliersRepo;
    //---新增---
    // 新增一張採購單
    public PurchaseOrderResponseDTO insertPurchaseOrder(PurchaseOrderCreateDTO dto) {
    // supplierId → Supplier Entity
    Suppliers supplier = suppliersRepo.findById(dto.getSupplierId())
        .orElseThrow(() -> new IllegalArgumentException("找不到此供應商"));

    // DTO → Entity
    PurchaseOrders purchaseOrder = new PurchaseOrders();

    purchaseOrder.setSupplier(supplier);
    purchaseOrder.setStatus(dto.getStatus());
    purchaseOrder.setCreatedBy(dto.getCreatedBy());
    purchaseOrder.setApprovedBy(dto.getApprovedBy());
    purchaseOrder.setTotal(dto.getTotal());
    purchaseOrder.setExpectedDeliveryDate(
            dto.getExpectedDeliveryDate()
    );
    PurchaseOrders saved = purchaseOrdersRepo.save(purchaseOrder);
    // Entity → ResponseDTO
    return PurchaseOrderResponseDTO.toResponseDTO(saved);
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