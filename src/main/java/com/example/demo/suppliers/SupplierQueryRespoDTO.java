package com.example.demo.suppliers;

import lombok.Data;

@Data
public class SupplierQueryRespoDTO {

    private Long id;

    private String name;

    private String callingCode;

    private String phone;

    private String extension;

    private String address;

    private String email;

    private SupplierStatus status;


    public static SupplierQueryRespoDTO fromEntity(
            Suppliers supplier) {

        SupplierQueryRespoDTO dto =
                new SupplierQueryRespoDTO();

        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setCallingCode(supplier.getCallingCode());
        dto.setPhone(supplier.getPhone());
        dto.setExtension(supplier.getExtension());
        dto.setAddress(supplier.getAddress());
        dto.setEmail(supplier.getEmail());
        dto.setStatus(supplier.getStatus());

        return dto;
    }
}
