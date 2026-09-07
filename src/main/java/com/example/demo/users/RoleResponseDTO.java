package com.example.demo.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {

    private Long id;
    private String roleName;
    private String description;

    public static RoleResponseDTO fromEntity(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleResponseDTO(role.getId(), role.getRoleName(), role.getDescription());
    }

}