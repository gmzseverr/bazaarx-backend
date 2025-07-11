package com.bazaarx.bazaarxbackend.repo;

import com.bazaarx.bazaarxbackend.entity.Product;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByBrandIgnoreCase(String brand);
    List<Product> findByCategoryIgnoreCase(String category);

    @Query(value = "{}", fields = "{'category' : 1}")
    List<Product> findAllCategories();

    @Aggregation(pipeline = {
            "{ $sample: { size: 50 } }"
    })
    List<Product> findRandomProducts();



}