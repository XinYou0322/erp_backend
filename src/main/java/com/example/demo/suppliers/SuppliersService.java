package com.example.demo.suppliers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuppliersService {
    
    private final SuppliersRepository suppliersRepo;

    //---新增---
    //單筆 ---完結版(暫)
    //email不重複才能新增
    public SuppliersDTO insertSupplier(SuppliersDTO dto) {
        if (suppliersRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email 已存在");
            //OR return "此email已存在"
        }
        if (suppliersRepo.existsByPhone(dto.getPhone())) {
        throw new IllegalArgumentException("電話已存在");
        }
        Suppliers suppliers = new Suppliers();
        suppliers.setName(dto.getName());
        suppliers.setPhone(dto.getPhone());
        suppliers.setAddress(dto.getAddress());
        suppliers.setEmail(dto.getEmail());
        Suppliers savedSupplier = suppliersRepo.save(suppliers);
        return SuppliersDTO.fromDto(savedSupplier);
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
                && suppliersRepo.existsByPhone(newSupplierDTO.getPhone())) {

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

    //多筆
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

    //全部
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
    public void deleteSupplier(Long id) {
        if (!suppliersRepo.existsById(id)) {
            throw new IllegalArgumentException("找不到供應商");
        }
        suppliersRepo.deleteById(id);
    }
    

}
