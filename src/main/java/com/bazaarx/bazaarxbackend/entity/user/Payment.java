package com.bazaarx.bazaarxbackend.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document // If using MongoDB
public class Payment {
    @Id
    private String id;
    private String cardholderName;
    private String tokenizedCardNumber;
    private String lastFourDigits;
    private String expiryMonth;
    private String expiryYear;
    private String cardBrand;
    private Date createdAt;
}
