package com.bazaarx.bazaarxbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private String productId;
    private String productName;
    private String imageUrl;
    private double priceAtOrder;
    private int quantity;
}