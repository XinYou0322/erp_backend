package com.example.demo.bom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Long> {

	  List<Bom> findByProductId(Long productId);
}
