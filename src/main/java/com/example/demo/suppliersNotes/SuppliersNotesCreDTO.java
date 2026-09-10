package com.example.demo.suppliersNotes;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data 
public class SuppliersNotesCreDTO {

    @NotBlank(message = "備註不可為空")
    @Size(max = 200, message = "備註不可超過200字")
    private String remark;


    
}
