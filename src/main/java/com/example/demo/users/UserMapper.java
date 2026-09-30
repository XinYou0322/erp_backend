package com.example.demo.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roleLevel", target = "roleLevel")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "salary", target = "salary")
    // Codex 修改：沿用前端既有的 department.name 回傳格式。
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "departmentName", target = "department.name")
    UserResponseDTO toDto(User user);
}
