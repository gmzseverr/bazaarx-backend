package com.bazaarx.bazaarxbackend.service.user;

import com.bazaarx.bazaarxbackend.dto.CreateOrderRequestDTO;
import com.bazaarx.bazaarxbackend.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(String userId, CreateOrderRequestDTO requestDTO);
    List<OrderDto> getUserOrders(String userId);

}