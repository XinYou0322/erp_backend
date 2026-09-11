package com.example.demo.suppliersNotes;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.suppliers.Suppliers;
import com.example.demo.suppliers.SuppliersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuppliersNotesService {

	 private final SuppliersRepository suppliersRepo;

	    private SuppliersNotesRepository suppliersNotesRepo;
	    
	    
	    //---新增---
	    //單筆
	   @Transactional
	    public SuppliersNotesRespoDTO createNote(
	        Long supplierId,
	        SuppliersNotesCreDTO createDTO
	      //,Long loginUserId
	        ) {

	    //找供應商
	    Suppliers supplier = suppliersRepo.findById(supplierId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("找不到供應商")
	            );

	    // // 找user
	    // Users creator = usersRepository.findById(loginUserId)
	    //         .orElseThrow(() ->
	    //                 new IllegalArgumentException("找不到登入者資料")
	    //         );

	    SupplierNotes note = new SupplierNotes();

	    note.setSupplier(supplier);
	    note.setRemark(createDTO.getRemark());
	    //note.setCreatedBy(creator);

	    SupplierNotes savedNote = suppliersNotesRepo.save(note);

	    return SuppliersNotesRespoDTO.fromEntity(savedNote);
	}
}
