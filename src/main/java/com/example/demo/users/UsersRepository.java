package com.example.demo.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    // 登入驗證與 JWT 簽發時使用
    @EntityGraph(attributePaths = { "role" })
    Optional<User> findByUsername(String username);

    // 登入驗證時支援使用 帳號 (Username) 或 信箱 (Email)
    @EntityGraph(attributePaths = { "role" })
    Optional<User> findByUsernameOrEmail(String username, String email);

    // 檢查帳號是否已存在（註冊/新增員工時使用）
    boolean existsByUsername(String username);

    // 檢查 Email 是否已存在
    boolean existsByEmail(String email);

    // 依據 Email 查詢
    Optional<User> findByEmail(String email);

    // 依據部門查詢員工
    // @EntityGraph(attributePaths = { "role" })
    // List<User> findByDepartmentId(Long departmentId);

    // 檢查是否有使用者關聯特定角色
    boolean existsByRoleId(Long roleId);

    // 查詢全部使用者（解決 N+1）
    @Override
    @EntityGraph(attributePaths = { "role" })
    List<User> findAll();

    // 分頁查詢（解決 N+1）
    @Override
    @EntityGraph(attributePaths = { "role" })
    Page<User> findAll(Pageable pageable);

    // 關鍵字搜尋（姓名或帳號模糊查詢，支援分頁）
    @EntityGraph(attributePaths = { "role" })
    Page<User> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String name, String username,
            Pageable pageable);

}