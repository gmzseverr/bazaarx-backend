package com.bazaarx.bazaarxbackend.service;

import com.bazaarx.bazaarxbackend.dto.CategoryDTO;
import com.bazaarx.bazaarxbackend.dto.ProductResponse;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface ProductService {
    List<ProductResponse> findAll();
    ProductResponse findById(String id);
    List<ProductResponse> findByName(String name);
    List<ProductResponse> findByBrand(String brand);
    List<String> findAllBrands();
    List<ProductResponse> findByCategory(String category);
    List<String> findAllCategories();
    List<CategoryDTO> getAllCategoriesWithProducts();
    List<ProductResponse> findRandomProducts();
}
