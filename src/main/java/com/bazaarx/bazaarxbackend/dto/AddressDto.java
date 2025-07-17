package com.bazaarx.bazaarxbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String id;
    private String title;
    private String street;
    private String addressLine;
    private String city;
    private String zipCode;
    private String country;
    private String phone;
}
