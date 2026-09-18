	package com.example.demo.suppliers;

import java.util.List;
import java.util.ArrayList;

//import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.users.User;
import com.example.demo.suppliers.SupplierRespoDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




//@Controller   
@RestController
@RequiredArgsConstructor
public class SuppliersController {
    private final SuppliersService suppliersService;

    //---新增---
    //單筆 ---暫
    @PostMapping("/api/Supplier/add")
    public ResponseEntity<SupplierRespoDTO> addSupplier(@Valid @RequestBody SupplierCreDTO credto,
    		 											@RequestParam Long loginUserId//測試用
    		 											) {
       //Long loginUserId = userUtil.getUserId();
        return ResponseEntity
            .status(HttpStatus.CREATED).body(suppliersService.insertSupplier(credto, loginUserId));
        // return "新增成功";
    }

    //多筆 ---暫
    @PostMapping("/api/Suppliers/addAll")
    public ResponseEntity<List<SupplierRespoDTO>> addSuppliers(
            @Valid@RequestBody@NotEmpty(message = "供應商清單不可為空") 
            List<@Valid SupplierCreDTO> dtoList,
            @RequestParam Long loginUserId //測試用
            ) {

    	 //Long loginUserId = userUtil.getUserId();

        return ResponseEntity
                .status(HttpStatus.CREATED) 
                .body(suppliersService.insertSuppliers(dtoList,loginUserId));
               
    }


    //---修改--- 暫
    @PatchMapping ("/api/Supplier/update/{id}")
    public ResponseEntity<SupplierRespoDTO> updateSupplier(@PathVariable Long id,
            												@Valid @RequestBody SuppliersUpdateDTO updateDTO) {

        return ResponseEntity.ok(suppliersService.updateSupplier(id,updateDTO));
    }


    //---查詢--- ---戰
    //單筆
    @GetMapping("/api/Supplier/find/{id}")
    public ResponseEntity<SupplierRespoDTO> findSupplierById(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            suppliersService.findSupplierById(id)
    );
}
    //多筆
    @GetMapping("/api/Supplier/findAll")
    public ResponseEntity<SupplierMultiQueryRespoDTO> findSuppliersByIds(
        @RequestParam("ids") List<Long> ids) {

    return ResponseEntity.ok(
            suppliersService.findByIds(ids)
    );
}
    //全部
    @GetMapping("/api/Supplier/All")
    public ResponseEntity<List<SupplierQueryRespoDTO>> findSuppliersAll() {
        return ResponseEntity.ok(
            suppliersService.listAllSuppliers()
    );
    }
    //分頁
    @GetMapping("/api/Supplier/page")
    public ResponseEntity<Page<SupplierQueryRespoDTO>> findSupplierPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) SupplierStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SupplierQueryRespoDTO> result =
                suppliersService.findSupplierPage(
                        keyword,
                        status,
                        page,
                        size
                );

        return ResponseEntity.ok(result);
    }
    //---刪除---
    //單筆
    @DeleteMapping("/api/Supplier/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {

    suppliersService.deleteSupplier(id);

    return ResponseEntity.noContent().build();
}
    //多筆
    @DeleteMapping("/api/Supplier/delete")
    public ResponseEntity<SupplierDeleteResultDTO> deleteSuppliers(
            @RequestBody List<Long> ids) {


        return ResponseEntity.ok(suppliersService.deleteSuppliers(ids));
    }
}
