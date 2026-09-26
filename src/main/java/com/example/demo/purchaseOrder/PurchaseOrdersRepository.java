package com.example.demo.purchaseOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, Long> {
	
	boolean existsBySupplierId(Long id);

    @EntityGraph(attributePaths = { "supplier", "items", "items.material" })
    List<PurchaseOrders> findByExpectedDeliveryDateAndStatusInOrderByOrderNumberAsc(
            LocalDate expectedDeliveryDate,
            Collection<PurchaseOrdersStatus> statuses);

    @Query("""
            SELECT po
            FROM PurchaseOrders po
            WHERE po.status IN :statuses
            ORDER BY
                CASE WHEN po.expectedDeliveryDate IS NULL THEN 1 ELSE 0 END,
                po.expectedDeliveryDate ASC,
                po.orderNumber ASC
            """)
    List<PurchaseOrders> findReceivableOrderByExpectedDeliveryDate(
            @Param("statuses") Collection<PurchaseOrdersStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT po FROM PurchaseOrders po WHERE po.id = :id")
    Optional<PurchaseOrders> findByIdForReceiving(@Param("id") Long id);

    // 列表與 countQuery 都套用狀態範圍及建立人，避免頁數與內容不一致。
    // createdByUserId 為 null 表示總覽；自己頁籤使用 Session 的登入者 ID。
    @Query(
            value = """
                SELECT DISTINCT po
                FROM PurchaseOrders po

                LEFT JOIN po.items item
                LEFT JOIN item.material material

                WHERE po.status IN :visibleStatuses
                AND (:createdByUserId IS NULL OR po.createdBy.id = :createdByUserId)
                AND
                    (:status IS NULL
                        OR po.status = :status)

                AND
                    (:supplierId IS NULL
                        OR po.supplier.id = :supplierId)

                AND (:minAmount IS NULL OR po.total >= :minAmount)
                AND (:maxAmount IS NULL OR po.total <= :maxAmount)

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

                        OR CAST(po.total AS string)
                           LIKE CONCAT('%', :keyword, '%')

                        OR LOWER(po.createdBy.name)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))

                        OR LOWER(po.supplier.name)
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

                WHERE po.status IN :visibleStatuses
                AND (:createdByUserId IS NULL OR po.createdBy.id = :createdByUserId)
                AND
                    (:status IS NULL
                        OR po.status = :status)

                AND
                    (:supplierId IS NULL
                        OR po.supplier.id = :supplierId)

                AND (:minAmount IS NULL OR po.total >= :minAmount)
                AND (:maxAmount IS NULL OR po.total <= :maxAmount)

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

                        OR CAST(po.total AS string)
                           LIKE CONCAT('%', :keyword, '%')

                        OR LOWER(po.createdBy.name)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))

                        OR LOWER(po.supplier.name)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))

                        OR LOWER(material.name)
                           LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                """
        )
        Page<PurchaseOrders> searchPurchaseOrders(

                // 頁籤查詢條件；既有收貨查詢不變。
                @Param("visibleStatuses") List<PurchaseOrdersStatus> visibleStatuses,
                @Param("createdByUserId") Long createdByUserId,
                @Param("status")
                PurchaseOrdersStatus status,

                @Param("supplierId")
                Long supplierId,

                @Param("minAmount")
                java.math.BigDecimal minAmount,

                @Param("maxAmount")
                java.math.BigDecimal maxAmount,

                @Param("startDateTime")
                LocalDateTime startDateTime,

                @Param("endDateTime")
                LocalDateTime endDateTime,

                @Param("keyword")
                String keyword,

                Pageable pageable
        );
}
