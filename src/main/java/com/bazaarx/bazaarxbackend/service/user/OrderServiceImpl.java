package com.bazaarx.bazaarxbackend.service.user;


import com.bazaarx.bazaarxbackend.dto.CreateOrderRequestDTO;
import com.bazaarx.bazaarxbackend.dto.OrderDto;
import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.entity.user.*;
import com.bazaarx.bazaarxbackend.exceptions.ResourceNotFoundException;
import com.bazaarx.bazaarxbackend.repo.OrderRepository;
import com.bazaarx.bazaarxbackend.repo.ProductRepository;
import com.bazaarx.bazaarxbackend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDto createOrder(String userId, CreateOrderRequestDTO requestDTO) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getCart().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double subtotal = 0;

        for (String productId : user.getCart()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

            double price = product.getPrice();
            if (product.getDiscount() > 0)   {
                price -= price * product.getDiscount() / 100.0;
            }

            OrderItem item = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0) : null,
                    price,
                    1
            );
            orderItems.add(item);
            subtotal += price;
        }

        Address address = user.getAddresses().stream()
                .filter(a -> a.getId().equals(requestDTO.getSelectedAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Payment payment = user.getPayments().stream()
                .filter(p -> p.getId().equals(requestDTO.getSelectedPaymentMethodId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        double shipping = subtotal < 50 ? 10 : 0;
        double total = subtotal + shipping;

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setShippingAddress(address);
        order.setPaymentMethod(payment);
        order.setTotalAmount(total);
        order.setCreatedAt(new Date());

        Order savedOrder = orderRepository.save(order);
        user.setCart(new ArrayList<>());
        userRepository.save(user);

        return modelMapper.map(savedOrder, OrderDto.class);
    }



    @Override
    public List<OrderDto> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }


}
