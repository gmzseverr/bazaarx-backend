package com.bazaarx.bazaarxbackend.mapper;

import com.bazaarx.bazaarxbackend.dto.ProductResponse;
import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.entity.ProductSizeDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public static ProductResponse toDto(Product product) {
        if (product == null) return null;


        List<ProductSizeDetail> sizeDetails = product.getSize().stream()
                .map(size -> new ProductSizeDetail(size.getValue(), size.getStockStatus()))
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .size(sizeDetails)
                .category(product.getCategory())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .color(product.getColor())
                .sku(product.getSku())
                .images(product.getImages())
                .brand(product.getBrand())
                .build();
    }
}
