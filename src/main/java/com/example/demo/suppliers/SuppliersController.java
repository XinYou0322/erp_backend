//package com.example.demo.suppliers;
//
//import java.util.List;
//import java.util.ArrayList;
//
////import org.springframework.stereotype.Controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import jakarta.validation.Valid;
//
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//
//
//
////@Controller   
//@RestController
//@RequiredArgsConstructor
//public class SuppliersController {
//    private final SuppliersService suppliersService;
//
//    //---新增---
//    //單筆
//    @PostMapping("/api/supplier/add")
//    public ResponseEntity<SupplierRespoDTO> addSupplier(@Valid @RequestBody SupplierCreDTO credto) {
//       
//        return ResponseEntity
//            .status(HttpStatus.CREATED).body(suppliersService.insertSupplier(credto));
//        // return "新增成功";
//    }
//
//    //多筆
//    @PostMapping("/api/suppliers/addAll")
//    public ResponseEntity<List<SuppliersDTO>> addSuppliers(@RequestBody List<@Valid SuppliersDTO> ListDto) {
//        //List<Suppliers> addAllSuppliers = new ArrayList<>();
//        return ResponseEntity
//            .status(HttpStatus.CREATED).body(suppliersService.insertSuppliers(ListDto));
//    }
//
//    //---修改---
//    @PatchMapping ("/api/update/{id}")
//    public ResponseEntity<SuppliersDTO> putMethodName(@PathVariable Long id, @Valid @RequestBody SuppliersUpdateDTO dto) {
//       
//        
//        return ResponseEntity.ok(
//            suppliersService.updateSupplier(id, dto)
//    );
//    }
//
//    //---查詢---
//    //單筆
//    @GetMapping("/api/supplier/{id}")
//    public ResponseEntity<SuppliersDTO> findSupplierById(
//        @PathVariable Long id) {
//
//    return ResponseEntity.ok(
//            suppliersService.findSupplierById(id)
//    );
//}
//    //多筆
//    @GetMapping("/api/suppliers")
//    public ResponseEntity<SuppliersQueryResultDTO> findSuppliersByIds(
//        @RequestParam List<Long> ids) {
//
//    return ResponseEntity.ok(
//            suppliersService.findSuppliersById(ids)
//    );
//}
//    //全部
//    @GetMapping("/api/suppliers/All")
//    public ResponseEntity<List<SuppliersDTO>> findSuppliersAll() {
//        return ResponseEntity.ok(
//            suppliersService.listAllSuppliers()
//    );
//    }
//
//    //---刪除---
//    //單筆
//    @DeleteMapping("/api/supplier/{id}")
//    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
//
//    suppliersService.deleteSupplier(id);
//
//    return ResponseEntity.noContent().build();
//}
//    //多筆
//    @DeleteMapping("/api/suppliers")
//    public ResponseEntity<List<Long>> deleteSuppliers(
//        @RequestParam List<Long> ids) {
//
//    List<Long> notFoundIds =
//            suppliersService.deleteSuppliers(ids);
//
//    return ResponseEntity.ok(notFoundIds);
//}
//
//}
