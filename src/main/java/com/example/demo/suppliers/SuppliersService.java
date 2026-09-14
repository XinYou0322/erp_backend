package com.example.demo.suppliers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.purchaseOrder.PurchaseOrdersRepository;
import com.example.demo.suppliersNotes.SupplierNotes;
import com.example.demo.suppliersNotes.SuppliersNotesCreDTO;
import com.example.demo.suppliersNotes.SuppliersNotesRepository;
import com.example.demo.suppliersNotes.SuppliersNotesRespoDTO;
import com.example.demo.suppliersNotes.SuppliersNotesService;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuppliersService {
    
    private final SuppliersRepository suppliersRepo;
    private final SuppliersNotesRepository suppliersNotesRepo;
    private final UsersRepository usersRepo;
    private final PurchaseOrdersRepository purchaseOrderRepo;
    private final SuppliersNotesService suppliersNotesService;
    
    
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
        	
        	SuppliersNotesRespoDTO savedNote =suppliersNotesService.createNote(
        			savedSupplier.getId(),
        			createDTO.getSupplierNotes(),
        		    loginUserId
        		      );
        	return SupplierRespoDTO.fromEntity(savedSupplier,savedNote);
        	
        }
        
        return SupplierRespoDTO.fromEntity(savedSupplier);
        
    }
    //多筆 ---暫
    @Transactional
    public List<SupplierRespoDTO> insertSuppliers(
            List<SupplierCreDTO> dtoList,
            Long loginUserId) {

        List<SupplierRespoDTO> respoList =
                new ArrayList<>();

        for (SupplierCreDTO createDTO : dtoList) {

            // 重複利用單筆新增方法
            SupplierRespoDTO responseDTO =
                    insertSupplier(
                            createDTO,
                            loginUserId
                    );

            respoList.add(responseDTO);
        }

        return respoList;
    }

    //---修改--- ---暫
    public SupplierRespoDTO updateSupplier(Long id, SuppliersUpdateDTO newSupplierDTO) {

    // 先確認這筆供應商存不存在
    Suppliers supplier = suppliersRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("找不到供應商"));
    //改名字
    if(newSupplierDTO.getName()!= null){
        supplier.setName(newSupplierDTO.getName().trim());
    }
    //改電話
    //國際碼或電話，只要其中一個有修改
    if (newSupplierDTO.getCallingCode() != null
            || newSupplierDTO.getPhone() != null) {
    	
    	
    	String newCallingCode =
    	        newSupplierDTO.getCallingCode() != null
    	                ? newSupplierDTO.getCallingCode()
    	                : supplier.getCallingCode();
    	String newPhone =
    	        newSupplierDTO.getPhone() != null
    	                ? newSupplierDTO.getPhone()
    	                : supplier.getPhone();
    	if (suppliersRepo
    	        .existsByCallingCodeAndPhoneAndIdNot(
    	                newCallingCode,
    	                newPhone,
    	                id)) {

    	    throw new IllegalArgumentException("電話已存在");
    	}
    	  supplier.setCallingCode(newCallingCode);
          supplier.setPhone(newPhone);
    }
    //改地址
    if(newSupplierDTO.getAddress()!= null){
        supplier.setAddress(newSupplierDTO.getAddress().trim());
    }
    //改email
    if (newSupplierDTO.getEmail() != null) {
       
    	  String newEmail =newSupplierDTO.getEmail().trim().toLowerCase();
    	  if (suppliersRepo.existsByEmailIgnoreCaseAndIdNot(newEmail,id)) {

              throw new IllegalArgumentException("Email 已存在");
          }
    	  supplier.setEmail(newEmail);
    }
    //改狀態
    if (newSupplierDTO.getStatus() != null) {
        supplier.setStatus(newSupplierDTO.getStatus());
    }
    //儲存
    Suppliers savedSupplier = suppliersRepo.save(supplier);
    //Entity > RespoDTO
    return SupplierRespoDTO.fromEntity(savedSupplier);}

    //---查詢---
    //單筆 ---暫
    @Transactional(readOnly = true)
    public SupplierRespoDTO findSupplierById(Long id) {

    Suppliers supplier = suppliersRepo.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException("找不到供應商"));

    return SupplierRespoDTO.fromEntity(supplier);
}

    //多筆 ---暫
    @Transactional(readOnly = true)
    public SupplierMultiQueryRespoDTO findByIds(List<Long> ids) {
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
        List<SupplierQueryRespoDTO> dtoList = new ArrayList<>();

        for (Suppliers supplier : suppliers) {

            SupplierQueryRespoDTO dto = SupplierQueryRespoDTO.fromEntity(supplier);

            dtoList.add(dto);}
        SupplierMultiQueryRespoDTO result = new SupplierMultiQueryRespoDTO();

        result.setSuppliers(dtoList);
        result.setNotFoundIds(notFoundIds);
    return result;
	}

    //全部 ---戰
    //可在Repo宣告一個可排序的查詢方法 就不用用findAll
    @Transactional(readOnly = true)
    public List<SupplierQueryRespoDTO> listAllSuppliers(){
        List<Suppliers> suppliersList  = suppliersRepo.findAll();
        List<SupplierQueryRespoDTO> dtoList = new ArrayList<>();
        for (Suppliers supplier : suppliersList) {

        SupplierQueryRespoDTO dto = SupplierQueryRespoDTO.fromEntity(supplier);

        dtoList.add(dto);
    }

    return dtoList;
	}

    //---刪除---
    //單筆 ---
    @Transactional
    public void deleteSupplier(Long id) {
    	  Optional<Suppliers> optionalSupplier = suppliersRepo.findById(id);
    
    	  if (optionalSupplier.isEmpty()) {
    	        throw new IllegalArgumentException("找不到供應商");
    	    }
    	  if (purchaseOrderRepo.existsBySupplierId(id)) {
    	        throw new IllegalArgumentException(
    	                "此供應商已有採購紀錄，不能刪除，請改為停用"
    	        );
    	    }
    	  suppliersNotesRepo.deleteBySupplierId(id);

    	  Suppliers supplier = optionalSupplier.get();
    	  
    	  suppliersRepo.delete(supplier);
    
    }
    //多筆 ---
    @Transactional
    public SupplierDeleteResultDTO deleteSuppliers(List<Long> ids) {

        List<String> deletedSuppliers = new ArrayList<>();

        List<String> purchaseOrderSuppliers = new ArrayList<>();

        List<Long> notFoundIds = new ArrayList<>();

        for (Long id : ids) {

            //找 Supplier
            Optional<Suppliers> optionalSupplier = suppliersRepo.findById(id);


            // 找不到
            if (optionalSupplier.isEmpty()) { 
            	notFoundIds.add(id);
                continue;
            }

            Suppliers supplier = optionalSupplier.get();

            //檢查是否已有採購紀錄      
            //有採購紀錄 → 不刪除
            boolean hasPurchaseOrder =
                    purchaseOrderRepo.existsBySupplierId(id);

            if (hasPurchaseOrder) {
                purchaseOrderSuppliers.add( supplier.getName());
                continue;
            }
            suppliersNotesRepo.deleteBySupplierId(id);

            //沒有採購紀錄 → 刪除
            suppliersRepo.delete(supplier);

            // 記錄成功刪除的供應商名稱
            deletedSuppliers.add(supplier.getName());
        }
        //組裝結果
        SupplierDeleteResultDTO result = new SupplierDeleteResultDTO();

        result.setDeletedSuppliers( deletedSuppliers);

        result.setPurchaseOrderSuppliers( purchaseOrderSuppliers);

        result.setNotFoundIds( notFoundIds);
        //組合訊息
        String message = "";
        // 有成功刪除
        if (!deletedSuppliers.isEmpty()) {
        	message += String.join("、",deletedSuppliers  );
            message += " 已刪除成功。";
        }


        // 有不能刪除的
        if (!purchaseOrderSuppliers.isEmpty()) {
            message += String.join(
                    "、",
                    purchaseOrderSuppliers
            );
            message += " 已有採購紀錄，不可刪除。";
        }

        // 有找不到的 ID
        if (!notFoundIds.isEmpty()) {
           message += "找不到供應商 ID："
                    + notFoundIds
                    + "。";
        }
        result.setMessage(message);

        return result;
    }
}
