package com.bazaarx.bazaarxbackend.dto;

import com.bazaarx.bazaarxbackend.entity.user.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String id;
    private String userId;
    private List<OrderItemDto> items;
    private AddressDto shippingAddress;
    private PaymentDto paymentMethod;

    private double totalAmount;

    private LocalDateTime orderDate;
}
