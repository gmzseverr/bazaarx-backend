package com.bazaarx.bazaarxbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {
    private String selectedAddressId;
    private String selectedPaymentMethodId;
}