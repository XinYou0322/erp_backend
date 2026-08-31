package com.example.demo.bom;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BomController {

    public final BomService bomService;

    @PostMapping("/api/bom/add") // 新增配方
    public ResponseEntity<?> create(@RequestBody Bom bom) {

        Bom b = bomService.create(bom);

        return new ResponseEntity<>(b, HttpStatus.CREATED);
    }

    @GetMapping("/api/bom/product/{productId}") // 查詢某商品底下所有配方
    public ResponseEntity<?> findByProductId(@PathVariable Long productId) {

        List<Bom> list = bomService.findByProductId(productId);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/api/bomupdate/{id}") // 修改配方
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Bom bom) {

        Bom b = bomService.update(id, bom);

        return new ResponseEntity<>(b, HttpStatus.OK);
    }

    @DeleteMapping("/api/bomdelete/{id}") // 刪除配方
    public ResponseEntity<?> delete(@PathVariable Long id) {

        bomService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}