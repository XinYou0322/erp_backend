package com.example.demo.materials;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MaterialController {
public final MaterialsService MtSerivce;

@PostMapping("/api/material/add")//新增原料
public  ResponseEntity<?> create(@RequestBody Material material) {
    
	
	
	Material m=  MtSerivce.create(material); 
	
	return new ResponseEntity<>(m,HttpStatus.CREATED);
}
// 修改原物料
@PutMapping("/api/materialupdate/{id}")
public ResponseEntity<?> update(
        @PathVariable Long id,
        @RequestBody Material material) {
	Material m=MtSerivce.update(id, material);
    return new ResponseEntity<>(m,HttpStatus.OK);
}
// 根據 ID 查詢
@GetMapping("/api/materialfind/{id}")
public ResponseEntity<?> findById(@PathVariable Long id) {
	Material m =MtSerivce.findById(id);
    return new ResponseEntity<>(m,HttpStatus.OK);
}

@DeleteMapping	("/api/materialdelete/{id}")
public ResponseEntity<?> deleteById(@PathVariable Long id) {
	Material m =MtSerivce.findById(id);
	MtSerivce.delete(id);
    return new ResponseEntity<>(m,HttpStatus.OK);
}


	
}
