package com.example.demo.suppliers;

import java.util.List;

import com.example.demo.suppliersNotes.SuppliersNotesCreDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierCreDTO {
	
	@NotBlank(message = "供應商名稱不可為空")
	@Size(max = 50, message = "供應商名稱不可以超過 50 個字")
	private String name;
	
	@NotBlank(message = "電話國碼不可為空")
	@Pattern(regexp = "^\\+[1-9][0-9]{0,2}$",message = "電話國碼格式錯誤，例如：+886、+81、+1")
	//是 Bean Validation 的字串格式驗證註解
	//^           從字串開頭開始
	//[0-9]       只能是 0~9
	//{6,15}      長度 6~15
	//$           到字串結尾
	private String callingCode = "+886";
	
	@NotBlank(message = "電話不可為空")
    @Pattern(regexp = "^[0-9]{6,15}$", message = "電話只能包含數字，長度需為 6～15 碼")
	private String phone;
	
    @Pattern(regexp = "^[0-9]{1,10}$", message = "分機只能包含數字，最多 10 碼")
    private String extension;
	
	@NotBlank(message = "地址不可為空")
	@Size(max = 200, message = "地址不可以超過 50 個字")
	private String address;
	
	@NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式錯誤")
	private String email;
	
	// 供應商狀態
    // 預設 PENDING
    // 前端也可以指定
	private SupplierStatus status = SupplierStatus.PENDING;
	
	
	@Valid //告訴 Validation：這個物件裡面的欄位，也要繼續進去驗證
    private SuppliersNotesCreDTO supplierNotes;
	
	public Suppliers toEntity() {
		Suppliers supplier = new Suppliers();
		
		supplier.setName(name);
		supplier.setCallingCode(callingCode);
		supplier.setPhone(phone);
		supplier.setExtension(extension);
		supplier.setAddress(address);
		supplier.setEmail(email);
		supplier.setStatus(status);
		
		
		return supplier;
	}
	
}
