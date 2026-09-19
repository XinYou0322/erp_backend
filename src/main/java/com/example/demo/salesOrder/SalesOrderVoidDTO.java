package com.example.demo.salesOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SalesOrderVoidDTO {
	
    @NotBlank(message = "報廢原因不可為空")
    @Size(max = 1000, message = "報廢原因不可超過1000字")
    private String voidReason;

}
