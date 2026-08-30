package com.example.demo.suppliers;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppliersRepository extends JpaRepository<Suppliers, Long> {
   
    //檢查email是否已存在
    boolean existsByEmail(String email);
}
