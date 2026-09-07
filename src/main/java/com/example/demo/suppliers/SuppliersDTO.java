package com.example.demo.suppliers;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SuppliersDTO {
    private Long id;
    
    @NotBlank(message = "供應商名稱不可為空")
    private String name;

    @NotBlank(message = "電話不可為空")
    private String phone;

    @NotBlank(message = "地址不可為空")
    private String address;

    @NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式錯誤")
    private String email;

    //單筆
    public static SuppliersDTO fromDto(Suppliers suppliers) {
        SuppliersDTO dto = new SuppliersDTO();
        dto.setId(suppliers.getId());
        dto.setName(suppliers.getName());
        dto.setPhone(suppliers.getPhone());
        dto.setAddress(suppliers.getAddress());
        dto.setEmail(suppliers.getEmail());
        return dto;
    }
    //多筆 
    //Entity → DTO
    public static List<SuppliersDTO> fromDtos(List<Suppliers> suppliersList) {
    List<SuppliersDTO> dtoList = new ArrayList<>();
    for (Suppliers supplier : suppliersList) {
        // 將每個 Suppliers 物件轉換為 SuppliersDTO 物件
        SuppliersDTO dto = new SuppliersDTO();

        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setPhone(supplier.getPhone());
        dto.setAddress(supplier.getAddress());
        dto.setEmail(supplier.getEmail());

        dtoList.add(dto);
    }

    return dtoList;
}
    //DTO → Entity
    public static List<Suppliers> toEntities(List<SuppliersDTO> dtoList) {

    List<Suppliers> suppliersList = new ArrayList<>();

    for (SuppliersDTO dto : dtoList) {

        Suppliers supplier = new Suppliers();

        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setEmail(dto.getEmail());

        suppliersList.add(supplier);
    }

    return suppliersList;
}
}

