package com.example.demo.products;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;



import lombok.RequiredArgsConstructor;

	@RequiredArgsConstructor
@Service
public class ProductService {

private final ProductRepository productdRepo;
public Products create(Products products) {
	   products.setCostPrice(BigDecimal.ZERO);
    // 1. 儲存原物料
	Products savedProductsl = productdRepo.save(products);

 

    // 3. 回傳建立好的原物料
    return savedProductsl;
}
public Products findById(Long id) {
	  Optional<Products> p = productdRepo.findById(id);
	  return productdRepo
	            .findById(id)
	            .orElseThrow();
  }
public List<Products> findAll() {
	  
	  
    return productdRepo.findAll();
           
}


public  Products update(Long id, Products newProducts) {
	  Optional<Products> p = productdRepo.findById(id);
	  Products products = p.get();
	  products.setSku(newProducts.getSku());
	  products.setName(newProducts.getName());
	  products.setCategory(newProducts.getCategory());
	  products.setSellingPrice(newProducts.getSellingPrice());
	  products.setCostPrice(newProducts.getCostPrice());
	  products.setUnit(newProducts.getUnit());
	  products.setStatus(newProducts.getStatus());
	  
	  
	  

      return productdRepo.save(products);
  }


public void delete(Long id) {
	
	 productdRepo.deleteById(id);
	
}





		
}
