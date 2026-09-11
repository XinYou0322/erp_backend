package com.example.demo.suppliers;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppliersRepository extends JpaRepository<Suppliers, Long> {
   
    //檢查email是否已存在
    boolean existsByEmail(String email);	

    // 國際碼 + 電話 是否已存在
    boolean existsByCallingCodeAndPhone(String callingCode, String phone);
}
