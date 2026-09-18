package com.example.demo.suppliersNotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.suppliers.Suppliers;
import com.example.demo.suppliers.SuppliersDTO;
import com.example.demo.suppliers.SuppliersRepository;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuppliersNotesService {

	 	private final SuppliersRepository suppliersRepo;

	    private final SuppliersNotesRepository suppliersNotesRepo;
	    
	    private final UsersRepository  usersRepo;
	    
	    //---新增---
	    //單筆
	   @Transactional
	    public SuppliersNotesRespoDTO createNote(
	        Long supplierId,
	        SuppliersNotesCreDTO createDTO
	      ,Long loginUserId
	        ) {

	    //找供應商
	    Suppliers supplier = suppliersRepo.findById(supplierId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("找不到供應商")	
	            );

	    // 找user
	    User creator = usersRepo.findById(loginUserId)
	             .orElseThrow(() ->
	                     new IllegalArgumentException("找不到登入者資料")
	             );

	    SupplierNotes note = new SupplierNotes();

	    note.setSupplier(supplier);
	    note.setRemark(createDTO.getRemark());
	    note.setCreatedBy(creator);

	    SupplierNotes savedNote = suppliersNotesRepo.save(note);

	    return SuppliersNotesRespoDTO.fromEntity(savedNote);
	}
	   
	   	//---修改---
	   public SuppliersNotesRespoDTO updateNotes(
			   Long supplierId,
			   Long noteId,
			   SuppliersNotesCreDTO updateDTO
		      ,Long loginUserId
		      ) {
		//找供應商
		Suppliers supplier = suppliersRepo.findById(supplierId)
		            .orElseThrow(() ->
		                    new IllegalArgumentException("找不到供應商"));
		                    
		// 找user
		User creator = usersRepo.findById(loginUserId)
		           	             .orElseThrow(() ->
		           	                     new IllegalArgumentException("找不到登入者資料")
		           	             );   
		//找note
	    SupplierNotes note = suppliersNotesRepo.findById(noteId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("找不到備註"));
	    //note = 供應商的
		if (!note.getSupplier().getId().equals(supplierId)) {
			throw new IllegalArgumentException("此備註不屬於該供應商");
		}
		//建立人 = 修改人
		if (!note.getCreatedBy().getId().equals(loginUserId)) {
		    throw new IllegalArgumentException("你沒有權限修改這筆備註");
		}
		//修改 > 儲存
		note.setRemark(updateDTO.getRemark());
		SupplierNotes savedNote = suppliersNotesRepo.save(note);
		//Entity > DTO
		return SuppliersNotesRespoDTO.fromEntity(savedNote);
	   }
	   // ---查詢---
	   //查某供應商備註
	   @Transactional(readOnly = true)
	   public List<SuppliersNotesRespoDTO> findSupplierAllNoteById(Long supplierId) {

		   suppliersRepo.findById(supplierId)
           .orElseThrow(() ->
                   new IllegalArgumentException("找不到此供應商")
           );
		   List<SupplierNotes> notes =
		            suppliersNotesRepo.findBySupplierId(supplierId);
		   
		   List<SuppliersNotesRespoDTO> dtoList = new ArrayList<>();
		   for (SupplierNotes note : notes) {
		        SuppliersNotesRespoDTO dto =
		                SuppliersNotesRespoDTO.fromEntity(note);

		        dtoList.add(dto);
		    }
		    return dtoList;
	   }
	   //---刪除---
	   @Transactional
	    public String deleteNote(Long noteId) {

	        Optional<SupplierNotes> optionalNote =
	                suppliersNotesRepo.findById(noteId);

	        if (optionalNote.isEmpty()) {
	            return "找不到備註，ID：" + noteId;
	        }

	        SupplierNotes note = optionalNote.get();

	        suppliersNotesRepo.delete(note);

	        return "備註刪除成功，ID：" + noteId;
	    }
	   @Transactional
	    public List<String> deleteNotes(List<Long> noteIds) {

	        List<String> result = new ArrayList<>();

	        if (noteIds == null || noteIds.isEmpty()) {
	            result.add("請提供要刪除的備註 ID");
	            return result;
	        }

	        for (Long noteId : noteIds) {

	            Optional<SupplierNotes> optionalNote =
	                    suppliersNotesRepo.findById(noteId);

	            if (optionalNote.isEmpty()) {

	                result.add("找不到備註，ID：" + noteId);

	            } else {

	                SupplierNotes note = optionalNote.get();

	                suppliersNotesRepo.delete(note);

	                result.add("備註刪除成功，ID：" + noteId);
	            }
	        }

	        return result;
	    }

	   //依 noteId 查單筆備註
	   //某供應商備註依時間排序
	   //分頁查某供應商備註
	   //關鍵字搜尋備註
	   //依建立人查備註
	   //查全部備註(最不需要)
	   
	   
	   
	   
	   
	   
	   
	   
	   
}
