package com.example.demo.suppliers;

import java.util.List;
import java.util.ArrayList;

//import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




//@Controller   
@RestController
@RequiredArgsConstructor
public class SuppliersController {
    private final SuppliersService suppliersService;

    //新增單筆
    //驗證Email格式 AI建議放在DTO(未完待續)
    @PostMapping("/api/supplier/add")
    public Suppliers addSupplier(@RequestBody Suppliers supplier) {
        Suppliers addSuppliers = suppliersService.insertSupplier(supplier.getName(), supplier.getPhone(), supplier.getAddress(), supplier.getEmail());
        
        //DTO限制回傳資料
        return addSuppliers;
        // return "新增成功";
    }

    //新增多筆
    @PostMapping("/api/suppliers/addAll")
    public List<Suppliers> addSuppliers(@RequestBody List<Suppliers> suppliersList) {
        //List<Suppliers> addAllSuppliers = new ArrayList<>();
       

        return suppliersService.insertSuppliers(suppliersList);
    }

    //修改
    @PutMapping("/api/update/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PUT request
        
        return entity;
    }

    //查詢單筆
    @GetMapping("/api/supplier/{id}")
    public Suppliers getSupplierById(@PathVariable Long id) {
        return suppliersService.findSupplierById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到供應商"));
                //回傳狀態值
    }

    //查詢多筆
    @GetMapping("/api/supplier/list")
    public List<Suppliers> getSuppliersByIds(@RequestBody List<Long> ids) {
        List<Suppliers> suppliersList = suppliersService.findSuppliersById(ids);
        return suppliersList;
    }

    //接收查詢所有供應商
    @GetMapping("/api/supplier/all")
    public List<Suppliers> AllSuppliers() {
        List<Suppliers> allSuppliers = suppliersService.listAllSuppliers();

        //DTO限制回傳資料
        return allSuppliers;
    }
    
    //刪除



}
