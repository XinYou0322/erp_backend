package com.example.demo.bom.version;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BomVersionRepository extends JpaRepository<BomVersion, Long> {
    @Query("SELECT COALESCE(MAX(v.versionNumber), 0) FROM BomVersion v WHERE v.product.id = :productId")
    Integer findLatestVersionNumber(@Param("productId") Long productId);

    List<BomVersion> findByProductIdOrderByVersionNumberDesc(Long productId);
    Optional<BomVersion> findTopByProductIdOrderByVersionNumberDesc(Long productId);
}
