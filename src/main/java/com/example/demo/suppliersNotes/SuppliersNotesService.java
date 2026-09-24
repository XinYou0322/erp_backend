package com.example.demo.suppliersNotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
	    note.setRemark(createDTO.getRemark().trim());
	    note.setCreatedBy(creator);

	    SupplierNotes savedNote = suppliersNotesRepo.save(note);

	    return SuppliersNotesRespoDTO.fromEntity(savedNote);
	}
	   
	   	//---修改---
	    @Transactional
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
		note.setRemark(updateDTO.getRemark().trim());
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
		            suppliersNotesRepo.findBySupplierIdOrderByCreatedAtDesc(supplierId);
		   
		   List<SuppliersNotesRespoDTO> dtoList = new ArrayList<>();
		   for (SupplierNotes note : notes) {
		        SuppliersNotesRespoDTO dto =
		                SuppliersNotesRespoDTO.fromEntity(note);

		        dtoList.add(dto);
		    }
		    return dtoList;
	   }
	   // 分頁查詢；頁碼從 0 開始，支援每頁 5、10、30、50 筆。
	    @Transactional(readOnly = true)
	    public Page<SuppliersNotesRespoDTO> findSupplierNotesPage(
	            Long supplierId, int page, int size) {
	        if (page < 0) {
	            page = 0;
	        }
	        if (size != 4) {
	            size = 4;
	        }
	        suppliersRepo.findById(supplierId)
	                .orElseThrow(() -> new IllegalArgumentException("找不到此供應商"));

	        // 建立時間相同時，以 ID 確保換頁順序穩定。
	        var pageable = PageRequest.of(page, size,
	                Sort.by(Sort.Direction.DESC, "createdAt", "id"));
	        return suppliersNotesRepo.findBySupplierId(supplierId, pageable)
	                .map(SuppliersNotesRespoDTO::fromEntity);
	    }
	   //---刪除---
	    @Transactional
	    public String deleteNote(Long noteId, Long loginUserId) {
	        SupplierNotes note = suppliersNotesRepo.findById(noteId)
	                .orElseThrow(() -> new IllegalArgumentException("找不到備註，ID：" + noteId));

	        usersRepo.findById(loginUserId)
	                .orElseThrow(() -> new IllegalArgumentException("找不到登入者資料"));

	        //原本修改有限制建立者，刪除卻任何人都能刪；統一成只有建立者能刪。
	        validateNoteOwner(note, loginUserId);
	        suppliersNotesRepo.delete(note);

	        return "備註刪除成功，ID：" + noteId;
	    }
	    private void validateNoteOwner(SupplierNotes note, Long loginUserId) {
	        if (!note.getCreatedBy().getId().equals(loginUserId)) {
	            throw new IllegalArgumentException("你沒有權限操作這筆備註");
	        }
	    }
	    @Transactional
	    public List<String> deleteNotes(List<Long> noteIds, Long loginUserId) {
	        if (noteIds == null || noteIds.isEmpty()) {
	            throw new IllegalArgumentException("請提供要刪除的備註 ID");
	        }

	        usersRepo.findById(loginUserId)
	                .orElseThrow(() -> new IllegalArgumentException("找不到登入者資料"));

	        List<SupplierNotes> notes = new ArrayList<>();
	        for (Long noteId : noteIds) {
	            SupplierNotes note = suppliersNotesRepo.findById(noteId)
	                    .orElseThrow(() -> new IllegalArgumentException("找不到備註，ID：" + noteId));
	            validateNoteOwner(note, loginUserId);
	            notes.add(note);
	        }

	        //全部檢查通過後才真正刪除，避免多筆刪除出現只刪一半的商業狀態。
	        suppliersNotesRepo.deleteAll(notes);

	        List<String> result = new ArrayList<>();
	        for (SupplierNotes note : notes) {
	            result.add("備註刪除成功，ID：" + note.getId());
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
