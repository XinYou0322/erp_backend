package com.example.demo.productcategory;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;


    // 新增分類
    public ProductCategory create(
            ProductCategory category) {

        String name =
                category.getName().trim();


        categoryRepository
                .findByName(name)
                .ifPresent(existing -> {

                    throw new IllegalArgumentException(
                            "分類名稱已存在：" + name
                    );

                });


        category.setId(null);

        category.setName(name);
        category.setActive(true);

        return categoryRepository.save(category);
    }


    // 查全部
    public List<ProductCategory> findAll() {

        return categoryRepository.findAll();
    }


    // 查啟用中的分類
    public List<ProductCategory> findActive() {

        return categoryRepository
                .findByActiveTrueOrderByNameAsc();
    }


    // 修改分類名稱
    public ProductCategory update(
            Long id,
            ProductCategory newCategory) {

        ProductCategory category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "找不到商品分類 id=" + id
                                )
                        );


        category.setName(
                newCategory.getName().trim()
        );


        return categoryRepository.save(category);
    }


    // 啟用 / 停用
    public ProductCategory updateActive(
            Long id,
            Boolean active) {

        ProductCategory category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "找不到商品分類 id=" + id
                                )
                        );


        category.setActive(active);


        return categoryRepository.save(category);
    }
}