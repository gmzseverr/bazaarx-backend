package com.bazaarx.bazaarxbackend.dto;


import com.bazaarx.bazaarxbackend.entity.ProductSizeDetail;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private List<ProductSizeDetail> size;
    private String category;
    private double price;
    private double discount;
    private String color;
    private Long sku;
    private List<String> images;
    private String brand;
}

