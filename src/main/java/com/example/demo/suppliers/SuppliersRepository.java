package com.example.demo.suppliers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query(
    	    "SELECT s FROM Suppliers s " +
    	    "WHERE LOWER(s.name) LIKE LOWER(:searchText) " +
    	    "OR s.callingCode LIKE :searchText " +
    	    "OR s.phone LIKE :searchText " +
    	    "OR s.extension LIKE :searchText " +
    	    "OR LOWER(s.address) LIKE LOWER(:searchText) " +
    	    "OR LOWER(s.email) LIKE LOWER(:searchText)"
    	)
    	Page<Suppliers> searchByKeyword(
    	        @Param("searchText") String searchText,
    	        Pageable pageable
    	);
}
