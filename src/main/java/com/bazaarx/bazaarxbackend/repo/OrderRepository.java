package com.bazaarx.bazaarxbackend.repo;


import com.bazaarx.bazaarxbackend.entity.user.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);

}