package com.example.demo.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // 註冊為 Spring Bean，可在 Service 直接 @Autowired
public interface UserMapper {

    @Mapping(source = "role.id", target = "role.id")
    @Mapping(source = "role.roleName", target = "role.name")
    @Mapping(source = "role.description", target = "role.description")
    // @Mapping(source = "departmentId", target = "department.id")
    UserResponseDTO toDto(User user);

}
