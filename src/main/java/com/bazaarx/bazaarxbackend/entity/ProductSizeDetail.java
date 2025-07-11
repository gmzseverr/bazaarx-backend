package com.bazaarx.bazaarxbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSizeDetail {

    private String value;
    private StockStatus stockStatus;
}