package com.example.demo.suppliers;

import java.util.List;
import lombok.Data;

@Data 
public class SuppliersQueryResultDTO {
     // 查到的供應商
    private List<SuppliersDTO> suppliers;

    // 查不到的 ID
    private List<Long> notFoundIds;
}
