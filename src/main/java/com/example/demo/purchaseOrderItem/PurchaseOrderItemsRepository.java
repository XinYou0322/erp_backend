package com.example.demo.purchaseOrderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;



public interface PurchaseOrderItemsRepository extends JpaRepository<PurchaseOrderItems, Long>{

    @Query("""
        SELECT i.material.id, SUM(i.quantity)
        FROM PurchaseOrderItems i
        WHERE i.purchaseOrder.status = com.example.demo.purchaseOrder.PurchaseOrdersStatus.APPROVED
        GROUP BY i.material.id
    """)
    List<Object[]> sumApprovedQuantityByMaterial();

}
