package com.example.demo.users;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 1. 查詢所有角色清單 (用於後台下拉選單或列表)
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        List<RoleResponseDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // 2. 根據 ID 查詢特定角色
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable Long id) {
        RoleResponseDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    // 3. 建立新角色
    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO dto) {
        RoleResponseDTO created = roleService.createRole(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 4. 修改角色
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO dto) {
        RoleResponseDTO updated = roleService.updateRole(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 5. 刪除角色
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(Map.of(
                "message", "角色刪除成功"));
    }

}
