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

	   @Transactional
	    public SuppliersNotesRespoDTO createNote(
	        Long supplierId,
	        SuppliersNotesCreDTO createDTO
	      //,Long loginUserId
	        ) {

	    // 前端選擇的供應商 ID，要在後端取得真正的 Entity
	    Suppliers supplier = suppliersRepo.findById(supplierId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("找不到供應商")
	            );

	    // // 根據登入者 ID 取得真正的 Users Entity
	    // Users creator = usersRepository.findById(loginUserId)
	    //         .orElseThrow(() ->
	    //                 new IllegalArgumentException("找不到登入者資料")
	    //         );

	    SupplierNotes note = new SupplierNotes();

	    note.setSuppliers(supplier);
	    note.setContent(createDTO.getContent());
	    note.setCreatedBy(creator);

	    SupplierNotes savedNote =
	            suppliersNotesRepository.save(note);

	    return SupplierNoteResponseDTO.fromEntity(savedNote);
	}
}
