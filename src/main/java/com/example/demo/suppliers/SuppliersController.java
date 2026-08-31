package com.example.demo.suppliers;

import java.util.List;
import java.util.ArrayList;

//import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



//@Controller
@RestController
@RequiredArgsConstructor
public class SuppliersController {
    private final SuppliersService suppliersService;

    //新增
    @PostMapping("/api/supplier/add")
    public Suppliers addSupplier(@RequestBody Suppliers supplier) {
        Suppliers addSuppliers = suppliersService.insertSupplier(supplier.getName(), supplier.getPhone(), supplier.getAddress(), supplier.getEmail());
        
        //DTO限制回傳資料
        return addSuppliers;
        // return "新增成功";
    }
    
    

    //接收查詢所有供應商
    @GetMapping("/api/supplier/all")
    public List<Suppliers> AllSuppliers() {
        List<Suppliers> allSuppliers = suppliersService.listAllSuppliers();
        
        //DTO限制回傳資料
        return allSuppliers;
    }
    




}
