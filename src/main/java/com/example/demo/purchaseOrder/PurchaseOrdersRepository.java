package com.example.demo.purchaseOrder;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, Long> {
	
	boolean existsBySupplierId(Long id);

    @Query(
            value = """
                SELECT DISTINCT po
                FROM PurchaseOrders po

                LEFT JOIN po.items item
                LEFT JOIN item.material material

                WHERE
                    (:status IS NULL
                        OR po.status = :status)

                AND
                    (:supplierId IS NULL
                        OR po.supplier.id = :supplierId)

                AND
                    (:startDateTime IS NULL
                        OR po.createdAt >= :startDateTime)

                AND
                    (:endDateTime IS NULL
                        OR po.createdAt < :endDateTime)

                AND
                    (
                        :keyword IS NULL

                        OR LOWER(po.orderNumber)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))

                        OR LOWER(material.name)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                """,

            countQuery = """
                SELECT COUNT(DISTINCT po.id)
                FROM PurchaseOrders po

                LEFT JOIN po.items item
                LEFT JOIN item.material material

                WHERE
                    (:status IS NULL
                        OR po.status = :status)

                AND
                    (:supplierId IS NULL
                        OR po.supplier.id = :supplierId)

                AND
                    (:startDateTime IS NULL
                        OR po.createdAt >= :startDateTime)

                AND
                    (:endDateTime IS NULL
                        OR po.createdAt < :endDateTime)

                AND
                    (
                        :keyword IS NULL

                        OR LOWER(po.orderNumber)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))

                        OR LOWER(material.name)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                """
        )
        Page<PurchaseOrders> searchPurchaseOrders(

                @Param("status")
                PurchaseOrdersStatus status,

                @Param("supplierId")
                Long supplierId,

                @Param("startDateTime")
                LocalDateTime startDateTime,

                @Param("endDateTime")
                LocalDateTime endDateTime,

                @Param("keyword")
                String keyword,

                Pageable pageable
        );
}
