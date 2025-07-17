package com.bazaarx.bazaarxbackend.entity.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    private String id;
    private String title;
    private String street;
    private String addressLine;
    private String city;
    private String zipCode;
    private String country;
    private String phone;

}