package com.bazaarx.bazaarxbackend.controller;
import com.bazaarx.bazaarxbackend.dto.CategoryDTO;
import com.bazaarx.bazaarxbackend.dto.ProductResponse;

import com.bazaarx.bazaarxbackend.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) { 

        ProductResponse product = productService.findById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
    @GetMapping("/random")
    public List<ProductResponse> getRandomProducts() {
        return productService.findRandomProducts();
    }


    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return productService.findAllCategories();
    }
    @GetMapping("/categories/{categoryName}")
    public List<ProductResponse> getProductsByCategory(@PathVariable String categoryName) {
        return productService.findByCategory(categoryName);
    }

    @GetMapping("/brands")
    public List<String> getAllBrands() {
        return productService.findAllBrands();
    }

    @GetMapping("/brands/{brandName}")
    public List<ProductResponse> getProductsByBrand(@PathVariable String brandName) {
        return productService.findByBrand(brandName);
    }



}
