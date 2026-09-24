package com.example.demo.suppliersNotes;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SuppliersNotesRepository extends JpaRepository<SupplierNotes, Long>{
	 void deleteBySupplierId(Long supplierId); 
	 
	 //直接排序OrderBy
	 List<SupplierNotes> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);
	 
	 Page<SupplierNotes> findBySupplierId(Long supplierId, Pageable pageable);
}
