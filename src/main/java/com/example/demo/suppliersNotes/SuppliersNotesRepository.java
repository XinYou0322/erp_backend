package com.example.demo.suppliersNotes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SuppliersNotesRepository extends JpaRepository<SupplierNotes, Long>{
	 void deleteBySupplierId(Long supplierId); 
	 
	 List<SupplierNotes> findBySupplierId(Long supplierId);
}
