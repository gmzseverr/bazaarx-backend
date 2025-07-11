package com.bazaarx.bazaarxbackend.controller;


import com.bazaarx.bazaarxbackend.dto.ProductResponse;
import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;

import com.bazaarx.bazaarxbackend.mapper.ProductMapper; // Senin ProductMapper'ını import et
import com.bazaarx.bazaarxbackend.mapper.UserMapper; // UserMapper'ı import et
import com.bazaarx.bazaarxbackend.service.user.UserService; // UserService'i import et
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;


    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<ProductResponse>> getFavoriteProducts(@PathVariable String userId) {
        try {
            List<ProductResponse> favoriteDTOs = userService.getUserFavoriteProductResponses(userId);
            return ResponseEntity.ok(favoriteDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated.");
        }

        if (authentication.getPrincipal() instanceof ApplicationUser) {
            ApplicationUser user = (ApplicationUser) authentication.getPrincipal();
            return user.getId();
        }
        // Nadir durumlar veya testler için bir fallback. Normalde buraya düşmemeli.
        return authentication.getName();
    }





    @PostMapping("/favorites/{productId}")
    public ResponseEntity<Map<String, Boolean>> toggleProductFavorite(@PathVariable String productId, Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);

            boolean isFavoriteAfterToggle = userService.toggleFavoriteProduct(userId, productId);

            return ResponseEntity.ok(Collections.singletonMap("isLiked", isFavoriteAfterToggle));
        } catch (IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isLiked", false));
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("isLiked", false));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isLiked", false));
        }
    }

    @GetMapping("/favorites/status/{productId}")
    public ResponseEntity<Map<String, Boolean>> getFavoriteStatus(@PathVariable String productId, Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);

            boolean isLiked = userService.isProductFavoriteForUser(userId, productId);
            return ResponseEntity.ok(Collections.singletonMap("isLiked", isLiked));
        } catch (IllegalStateException e) {

            return ResponseEntity.ok(Collections.singletonMap("isLiked", false));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isLiked", false));
        }
    }



}