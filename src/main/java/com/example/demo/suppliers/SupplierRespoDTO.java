package com.example.demo.suppliers;

import com.example.demo.suppliersNotes.SupplierNotes;
import com.example.demo.suppliersNotes.SuppliersNotesRespoDTO;

import lombok.Data;

@Data
public class SupplierRespoDTO {
	
	private Long id;
	
	private String name;
	
	private String callingCode;
	
	private String phone;
	
	private String extension;
	
	private String address;
	
	private String email;
	
	private SupplierStatus status;
	
	private SuppliersNotesRespoDTO note;
	
	//沒有備註
	public static SupplierRespoDTO fromEntity(Suppliers supplier) {

        return fromEntity(supplier, null);
    }
	
	// Entity → ResponseDTO
    public static SupplierRespoDTO fromEntity(Suppliers supplier,SupplierNotes note) {

        SupplierRespoDTO dto = new SupplierRespoDTO();

        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setCallingCode(supplier.getCallingCode());
        dto.setPhone(supplier.getPhone());
        dto.setExtension(supplier.getExtension());
        dto.setAddress(supplier.getAddress());
        dto.setEmail(supplier.getEmail());
        dto.setStatus(supplier.getStatus());
        
        if (note != null) {
            dto.setNote(SuppliersNotesRespoDTO.fromEntity(note)); 
        }
        
        return dto;
    }
}
