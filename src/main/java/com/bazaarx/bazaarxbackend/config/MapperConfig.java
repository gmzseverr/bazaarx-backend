// src/main/java/com/bazaarx/bazaarxbackend/config/MapperConfig.java
package com.bazaarx.bazaarxbackend.config;

import com.bazaarx.bazaarxbackend.dto.UserResponseDTO;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections; // Collections sınıfını import edin
import java.util.stream.Collectors;


@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper;
    }
}