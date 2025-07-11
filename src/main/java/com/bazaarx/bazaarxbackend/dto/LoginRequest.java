package com.bazaarx.bazaarxbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email(message = "Email should be valid") @NotBlank(message = "Email cannot be empty") String email,
        @Size(min = 5, message = "Password must be at least 5 characters long") @NotBlank(message = "Password cannot be empty") String password
) { }