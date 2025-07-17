package com.bazaarx.bazaarxbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {


    @NotBlank(message = "Cardholder name cannot be blank")
    @Size(min = 2, max = 100, message = "Cardholder name must be between 2 and 100 characters")
    private String cardholderName;

    @NotBlank(message = "Card number cannot be blank")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Invalid card number format")

    private String cardNumber;

    @NotBlank(message = "Expiry month cannot be blank")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Invalid expiry month (MM)")
    private String expiryMonth;

    @NotBlank(message = "Expiry year cannot be blank")
    @Pattern(regexp = "^(20\\d{2}|\\d{2})$", message = "Invalid expiry year (YY or YYYY)")

    private String expiryYear;

    @NotBlank(message = "CVC cannot be blank")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "Invalid CVC (3 or 4 digits)")
    private String cvc;

    private String cardBrand;
}

