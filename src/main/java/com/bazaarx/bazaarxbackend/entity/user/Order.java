package com.bazaarx.bazaarxbackend.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    private String userId;

    private List<OrderItem> items;
    private double totalAmount;
    private Address shippingAddress;
    private Payment paymentMethod;

    private Date createdAt;
}
