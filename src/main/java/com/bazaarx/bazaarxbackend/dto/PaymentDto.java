package com.bazaarx.bazaarxbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String id;
    private String cardholderName;
    private String lastFourDigits;
    private String expiryMonth;
    private String expiryYear;
    private String cardBrand;
    private String createdAt;
}