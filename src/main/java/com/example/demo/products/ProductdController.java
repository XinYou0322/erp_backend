package com.example.demo.products;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.materials.Material;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductdController {
public final ProductService pdService;

@PostMapping("/api/product/add")//新增產品
public  ResponseEntity<?> create(@RequestBody Products product) {
    
	
	
	Products p=  pdService.create(product);
	
	return new ResponseEntity<>(p,HttpStatus.CREATED);
}
	
	
	
@GetMapping("/api/product/list") // 查詢全部
public ResponseEntity<?> findAll() {

    List<Products> list = pdService.findAll();

    return new ResponseEntity<>(list, HttpStatus.OK);
}

@GetMapping("/api/product/{id}") // 查詢單一
public ResponseEntity<?> findById(@PathVariable Long id) {

    Products p = pdService.findById(id);

    return new ResponseEntity<>(p, HttpStatus.OK);
}

@PutMapping("/api/productupdate/{id}") // 修改產品
public ResponseEntity<?> update(
        @PathVariable Long id,
        @RequestBody Products product) {

    Products p = pdService.update(id, product);

    return new ResponseEntity<>(p, HttpStatus.OK);
}

@DeleteMapping("/api/productdelete/{id}") // 刪除產品
public ResponseEntity<?> delete(@PathVariable Long id) {

    pdService.delete(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}
}
	
	
	
	
	
	
	
	
	
	
	

