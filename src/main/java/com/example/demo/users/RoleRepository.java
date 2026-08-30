package com.example.demo.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

    // 根據角色名稱查詢特定角色詳細資料
    Optional<Roles> findByRoleName(String roleName);

    // 檢查資料庫中是否存在指定的角色名稱
    boolean existsByRoleName(String roleName);

}
