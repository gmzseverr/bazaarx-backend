package com.bazaarx.bazaarxbackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private String name;
    private List<ProductResponse> products;
}
