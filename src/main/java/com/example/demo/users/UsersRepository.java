package com.example.demo.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

    // 登入驗證與 JWT 簽發時使用
    Optional<Users> findByUsername(String username);

    // 檢查帳號是否已存在（註冊/新增員工時使用）
    boolean existsByUsername(String username);

    // 依據部門查詢員工
    List<Users> findByDepartmentId(Long departmentId);

}