package com.example.demo.suppliers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.suppliersNotes.SupplierNotes;
import com.example.demo.suppliersNotes.SuppliersNotesRepository;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuppliersService {
    
    private final SuppliersRepository suppliersRepo;
    private final SuppliersNotesRepository suppliersNotesRepo;
    private final UsersRepository usersRepo;
    
    
    //---新增---
    //單筆 ---完結版(暫)
    @Transactional
    public SupplierRespoDTO insertSupplier(SupplierCreDTO createDTO
    		,Long loginUserId
    		) {
    	//---供應商資料新增---
    	//Email檢查
        if (suppliersRepo.existsByEmail(createDTO.getEmail())) {
            throw new IllegalArgumentException("Email 已存在");
            //OR return "此email已存在"
        }
        //電話檢查
        if (suppliersRepo.existsByCallingCodeAndPhone(createDTO.getCallingCode(),createDTO.getPhone())) {
        throw new IllegalArgumentException("電話已存在");
        }                
        //DTO -> Entity
        Suppliers supplier = createDTO.toEntity();
        //儲存
        Suppliers savedSupplier = suppliersRepo.save(supplier);
        
        //---備註判斷--- noteService
        if(createDTO.getSupplierNotes()!=null) {
        	//檢查登入者
        	User creator = usersRepo.findById(loginUserId)
        			.orElseThrow(() ->new IllegalArgumentException("找不到登入者資料"));
        	//備註Entity
        	SupplierNotes note = new SupplierNotes();
        	note.setRemark(createDTO.getSupplierNotes().getRemark());
        	note.setCreatedBy(creator);
        	
        	savedSupplier.addNote(note);
        	
        	SupplierNotes savedNote = suppliersNotesRepo.save(note);
        	
        	return SupplierRespoDTO.fromEntity( savedSupplier,savedNote);
        }
        
        return SupplierRespoDTO.fromEntity(savedSupplier);
        
    }
    //多筆 ---完結版(暫)
    public List<SuppliersDTO> insertSuppliers(List<SuppliersDTO> dtoList) {

        // DTO List → Entity List
        List<Suppliers> suppliersList =
            SuppliersDTO.toEntities(dtoList);
    return SuppliersDTO.fromDtos(suppliersRepo.saveAll(suppliersList));
    }

    //---修改--- ---完結版(暫)
    public SuppliersDTO updateSupplier(Long id, SuppliersUpdateDTO newSupplierDTO) {

    // 先確認這筆供應商存不存在
    Suppliers supplier = suppliersRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("找不到供應商"));
    //改名字
    if(newSupplierDTO.getName()!= null){
        supplier.setName(newSupplierDTO.getName());
    }
    //改電話
    if(newSupplierDTO.getPhone()!= null){
            //新電話等不等於舊電話(equals等於) && 資料庫有沒有這個新電話
        if (!newSupplierDTO.getPhone().equals(supplier.getPhone())
                && suppliersRepo.existsByCallingCodeAndPhone(newSupplierDTO.getCallingCode(),newSupplierDTO.getPhone())) {

            throw new IllegalArgumentException("電話已存在");
        }
        supplier.setPhone(newSupplierDTO.getPhone());
    }
    //改地址
    if(newSupplierDTO.getAddress()!= null){
        supplier.setAddress(newSupplierDTO.getAddress());
    }
    //改email
    if (newSupplierDTO.getEmail() != null) {
       
        if (!newSupplierDTO.getEmail().equals(supplier.getEmail())
                && suppliersRepo.existsByEmail(newSupplierDTO.getEmail())) {
            throw new IllegalArgumentException("Email 已存在");
        }
        supplier.setEmail(newSupplierDTO.getEmail());
    }
    Suppliers savedSupplier = suppliersRepo.save(supplier);
    return SuppliersDTO.fromDto(savedSupplier);
}

    //---查詢---
    //單筆 ---完結版(暫)
    public SuppliersDTO findSupplierById(Long id) {

    Suppliers supplier = suppliersRepo.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException("找不到供應商"));

    return SuppliersDTO.fromDto(supplier);
}

    //多筆 ---完結版(暫)
    public SuppliersQueryResultDTO findSuppliersById(List<Long> ids){
        //此id存不存在
        List<Suppliers> suppliers = suppliersRepo.findAllById(ids);
        List<Long> foundIds = new ArrayList<>();
        for (Suppliers supplier : suppliers) {
        foundIds.add(supplier.getId());
    }
        List<Long> notFoundIds = new ArrayList<>();
        for (Long id : ids) {
        if (!foundIds.contains(id)) {
            notFoundIds.add(id);
        }
    }
        List<SuppliersDTO> dtoList =
            SuppliersDTO.fromDtos(suppliers);
        
        SuppliersQueryResultDTO result =
            new SuppliersQueryResultDTO();
        result.setSuppliers(dtoList);
        result.setNotFoundIds(notFoundIds);

    return result;
	}

    //全部 ---完結版(暫)
    public List<SuppliersDTO> listAllSuppliers(){
        List<Suppliers> suppliersList  = suppliersRepo.findAll();
        List<SuppliersDTO> dtoList = new ArrayList<>();
        for (Suppliers supplier : suppliersList) {

        SuppliersDTO dto =
                SuppliersDTO.fromDto(supplier);

        dtoList.add(dto);
    }

    return dtoList;
	}

    //---刪除---
    //單筆 ---完結版(暫)
    public void deleteSupplier(Long id) {
        if (!suppliersRepo.existsById(id)) {
            throw new IllegalArgumentException("找不到供應商");
        }
        suppliersRepo.deleteById(id);
    }
    //多筆 ---完結版(暫)
    public List<Long> deleteSuppliers(List<Long> ids) {

    List<Long> notFoundIds = new ArrayList<>();

    for (Long id : ids) {

        if (suppliersRepo.existsById(id)) {
            suppliersRepo.deleteById(id);
        } else {
            notFoundIds.add(id);
        }
    }
    return notFoundIds;
}
}
