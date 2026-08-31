package com.example.demo.products;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.inventories.Inventory;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;

import lombok.RequiredArgsConstructor;

	@RequiredArgsConstructor
@Service
public class ProductService {
private final MaterialRepository MaterialRepo;
private final ProductdRepository ProductdRepo;
public Products create(Products products) {

    // 1. 儲存原物料
	Products savedProductsl = ProductdRepo.save(products);

 

    // 3. 回傳建立好的原物料
    return savedProductsl;
}
public Products findById(Long id) {
	  Optional<Products> p = ProductdRepo.findById(id);
	  
      return p.get();
             
  }
public List<Products> findAll() {
	  
	  
    return ProductdRepo.findAll();
           
}


public  Products update(Long id, Products newProducts) {
	  Optional<Products> p = ProductdRepo.findById(id);
	  Products products = p.get();
	  products.setSku(newProducts.getSku());
	  products.setName(newProducts.getName());
	  products.setCategory(newProducts.getCategory());
	  products.setSellingPrice(newProducts.getSellingPrice());
	  products.setCostPrice(newProducts.getCostPrice());
	  products.setUnit(newProducts.getUnit());
	  products.setStatus(newProducts.getStatus());
	  
	  
	  

      return ProductdRepo.save(products);
  }


public void delete(Long id) {
	
	 ProductdRepo.deleteById(id);
	
}





		
}
