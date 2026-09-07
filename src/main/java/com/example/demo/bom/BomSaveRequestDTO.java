package com.example.demo.bom;

import java.util.List;

import lombok.Data;

@Data
public class BomSaveRequestDTO {

    private Long productId;

    private List<BomItemRequestDTO> items;
}