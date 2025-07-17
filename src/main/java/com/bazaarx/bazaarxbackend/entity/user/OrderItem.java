package com.bazaarx.bazaarxbackend.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productId;
    private String productName;
    private String imageUrl;
    private double priceAtOrder;
    private int quantity;
}