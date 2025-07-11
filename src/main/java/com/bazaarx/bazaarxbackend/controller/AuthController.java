package com.bazaarx.bazaarxbackend.controller;

import com.bazaarx.bazaarxbackend.dto.AuthResponseDTO;
import com.bazaarx.bazaarxbackend.dto.LoginRequest;
import com.bazaarx.bazaarxbackend.dto.RegisterRequest;
import com.bazaarx.bazaarxbackend.dto.UserResponseDTO;
import com.bazaarx.bazaarxbackend.service.user.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationService authenticationService;


    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody RegisterRequest registerUser){

        return authenticationService.register(
                registerUser.fullName(),
                registerUser.email(),
                registerUser.password());

    }


    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid LoginRequest loginUser) {
        return authenticationService.login(
                loginUser.email(),
                loginUser.password());
    }
}
