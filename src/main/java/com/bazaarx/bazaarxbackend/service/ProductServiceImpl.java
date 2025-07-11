package com.bazaarx.bazaarxbackend.service;

import com.bazaarx.bazaarxbackend.dto.CategoryDTO;
import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.exception.ProductNotFoundException;
import com.bazaarx.bazaarxbackend.mapper.ProductMapper;
import com.bazaarx.bazaarxbackend.repo.ProductRepository;
import com.bazaarx.bazaarxbackend.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse findById(String id) {
        return productRepository.findById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException(id)); // Ürün bulunamazsa özel istisna fırlat
    }
    @Override
    public List<ProductResponse> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> findByBrand(String brand) {
        return productRepository.findByBrandIgnoreCase(brand).stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<String> findAllBrands() {
        return productRepository.findAll().stream()
                .map(Product::getBrand)
                .filter(Objects::nonNull)

                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> findByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category).stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)

                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAllCategoriesWithProducts() {
        return findAllCategories().stream()
                .map(category -> CategoryDTO.builder()
                        .name(category)
                        .products(findByCategory(category))
                        .build())
                .collect(Collectors.toList());
    }
    @Override
    public List<ProductResponse> findRandomProducts() {
        return productRepository.findRandomProducts().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }



}
