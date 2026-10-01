package com.example.demo.materialsettlement;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface DailyMaterialSettlementRepository
        extends JpaRepository<DailyMaterialSettlement, Long> {

    @EntityGraph(attributePaths = { "items", "items.material", "createdBy" })
    Optional<DailyMaterialSettlement> findBySettlementDate(LocalDate settlementDate);

    @EntityGraph(attributePaths = { "items", "items.material" })
    Optional<DailyMaterialSettlement> findFirstByStatusAndSettlementDateBeforeOrderBySettlementDateDesc(
            SettlementStatus status, LocalDate settlementDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT DISTINCT s
            FROM DailyMaterialSettlement s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.material
            WHERE s.id = :id
            """)
    Optional<DailyMaterialSettlement> findByIdForCompletion(@Param("id") Long id);
}
