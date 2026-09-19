package com.example.demo.suppliers;

import java.util.List;

import lombok.Data;

@Data
public class SupplierMultiQueryRespoDTO {

    // 查到的供應商資料
    private List<SupplierQueryRespoDTO> suppliers;

    // 查不到的 ID
    private List<Long> notFoundIds;
}
