package com.bazaarx.bazaarxbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String id;
    private String fullName;
    private String email;
    private Set<String> roles;

}