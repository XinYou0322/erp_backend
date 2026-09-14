package com.example.demo.suppliers;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppliersRepository extends JpaRepository<Suppliers, Long> {
   
    //檢查email是否已存在
    boolean existsByEmail(String email);	

    // 國際碼 + 電話 是否已存在
    boolean existsByCallingCodeAndPhone(String callingCode, String phone);
    
    // 檢查是否有「其他供應商」 使用相同 Email
    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id
    );

    // 檢查是否有「其他供應商」
    // 使用相同「國際碼 + 電話」
    boolean existsByCallingCodeAndPhoneAndIdNot(
            String callingCode,
            String phone,
            Long id
    );
}
