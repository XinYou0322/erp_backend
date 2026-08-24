package com.example.demo.materials;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MaterialController {
public final MaterialsService MtSerivce;

@PostMapping("/api/material/add")
public  ResponseEntity<?> create(@RequestBody Material material) {
    
	
	
	Material m=  MtSerivce.create(material); 
	
	return new ResponseEntity<>(m,HttpStatus.OK);
}


	
	
}
