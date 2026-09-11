package com.example.demo.productcategory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByName(String name);

    List<ProductCategory> findByActiveTrueOrderByNameAsc();
}