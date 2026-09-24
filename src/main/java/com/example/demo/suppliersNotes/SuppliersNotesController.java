package com.example.demo.suppliersNotes;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.suppliers.SupplierCreDTO;
import com.example.demo.suppliers.SupplierRespoDTO;
import com.example.demo.suppliers.SuppliersDTO;
import com.example.demo.suppliers.SuppliersService;
import com.example.demo.suppliers.SuppliersUpdateDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SuppliersNotesController {
	
	private final SuppliersNotesService suppliersNotesService;
	
	//---新增---
	//單筆
	@PostMapping("/api/supplierNote/{supplierId}")
	public ResponseEntity<SuppliersNotesRespoDTO> addNote(
	        @PathVariable Long supplierId,
	        @Valid @RequestBody SuppliersNotesCreDTO createDTO,
	        @RequestParam Long loginUserId
	) {

		//Long loginUserId = userUtil.getUserId();
	    SuppliersNotesRespoDTO result =
	            suppliersNotesService.createNote(
	                    supplierId,
	                    createDTO,
	                    loginUserId
	            );

	    return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(result);
	}
	//---修改---
	@PatchMapping("/api/supplierNote/update/{supplierId}/{noteId}")
	public ResponseEntity<SuppliersNotesRespoDTO> updateNote(
	        @PathVariable Long supplierId,
	        @PathVariable Long noteId,
	        @Valid @RequestBody SuppliersNotesCreDTO updateDTO,
	        @RequestParam Long loginUserId
	) {
		
		//Long loginUserId = userUtil.getUserId();
        
		
        return ResponseEntity.ok(suppliersNotesService.updateNotes(supplierId, noteId, updateDTO, loginUserId)
            );
    
    }
	//---查詢---
	@GetMapping("/api/supplierNote/supplier/{supplierId}")
    public ResponseEntity<Page<SuppliersNotesRespoDTO>> findSupplierNotesPage(
            @PathVariable("supplierId") Long supplierId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size) {
        return ResponseEntity.ok(
                suppliersNotesService.findSupplierNotesPage(supplierId, page, size));
    }
	//---刪除---
	//單筆
    @DeleteMapping("/api/supplierNote/{noteId}")
    public ResponseEntity<String> deleteNote(
            @PathVariable Long noteId,
            //【我新增】刪除也必須知道操作者，才能套用與修改相同的權限規則。
            @RequestParam Long loginUserId) {

        return ResponseEntity.ok(suppliersNotesService.deleteNote(noteId, loginUserId));
    }
    @DeleteMapping("/api/supplierNote/Many")
    public ResponseEntity<List<String>> deleteNotes(
            @RequestBody List<Long> noteIds,
            //【我新增】多筆刪除同樣驗證登入者是否為備註建立者。
            @RequestParam Long loginUserId) {

        return ResponseEntity.ok(suppliersNotesService.deleteNotes(noteIds, loginUserId));
    }

}
