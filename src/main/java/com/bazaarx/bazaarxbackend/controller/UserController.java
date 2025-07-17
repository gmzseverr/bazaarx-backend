// UserController.java
package com.bazaarx.bazaarxbackend.controller; // Doğru paket yolunu kontrol edin

import com.bazaarx.bazaarxbackend.dto.*;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser; // ApplicationUser import'unu tutun (optional, hata fırlatıldığında kullanılabilir)
import com.bazaarx.bazaarxbackend.exceptions.ResourceNotFoundException;
import com.bazaarx.bazaarxbackend.mapper.ProductMapper; // Bu mapper'a artık ihtiyaç kalmayabilir, kullanmıyorsunuz.
import com.bazaarx.bazaarxbackend.service.user.OrderService;
import com.bazaarx.bazaarxbackend.service.user.UserService; // UserService'i kullanıyoruz

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final ModelMapper modelMapper;


    @GetMapping("/favorites")
    public ResponseEntity<List<ProductResponse>> getFavoriteProducts(Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            List<ProductResponse> favoriteDTOs = userService.getUserFavoriteProductResponses(userId);
            return ResponseEntity.ok(favoriteDTOs);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/favorites/{productId}")
    public ResponseEntity<Map<String, Boolean>> toggleProductFavorite(@PathVariable String productId, Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            boolean isFavoriteAfterToggle = userService.addProductToFavorites(userId, productId);
            return ResponseEntity.ok(Collections.singletonMap("isLiked", isFavoriteAfterToggle));
        } catch (IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isLiked", false));
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("isLiked", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isLiked", false));
        }
    }
    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<Map<String, Boolean>> removeProductFromFavorites(@PathVariable String productId, Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            boolean isFavoriteAfterToggle = userService.removeProductFromFavorites(userId, productId);
            return ResponseEntity.ok(Collections.singletonMap("isLiked", isFavoriteAfterToggle));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isLiked", false));
        } catch (RuntimeException e) {
            System.err.println("Error removing product from favorites: " + e.getMessage()); // Log for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("isLiked", false));
        } catch (Exception e) {
            System.err.println("Internal server error removing product from favorites: " + e.getMessage()); // Log for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isLiked", false));
        }
    }
    @GetMapping("/favorites/status/{productId}")
    public ResponseEntity<Map<String, Boolean>> getFavoriteStatus(@PathVariable String productId, Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            boolean isLiked = userService.isProductFavoriteForUser(userId, productId);
            return ResponseEntity.ok(Collections.singletonMap("isLiked", isLiked));
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(Collections.singletonMap("isLiked", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isLiked", false));
        }
    }

    // *****CART

    @PostMapping("/cart/{productId}")
    public ResponseEntity<Map<String, Boolean>> addProductToCart(@PathVariable String productId, Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            boolean isAdded = userService.addProductToCart(userId, productId);
            return ResponseEntity.ok(Collections.singletonMap("isAdded", isAdded));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isAdded", false));
        } catch (ResourceNotFoundException e) { // Ürün veya kullanıcı bulunamazsa
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("isAdded", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isAdded", false));
        }
    }

    @DeleteMapping("/cart/{productId}")
    public ResponseEntity<Map<String, Boolean>> removeProductFromCart(@PathVariable String productId, Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            boolean isRemoved = userService.removeProductFromCart(userId, productId);
            return ResponseEntity.ok(Collections.singletonMap("isRemoved", isRemoved));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isRemoved", false));
        } catch (ResourceNotFoundException e) { // Ürün veya kullanıcı bulunamazsa
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("isRemoved", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isRemoved", false));
        }
    }
    @GetMapping("/cart")
    public ResponseEntity<List<ProductResponse>> getUserCartProducts(Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            List<ProductResponse> cartProducts = userService.getUserCartProducts(userId);
            return ResponseEntity.ok(cartProducts);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        } catch (ResourceNotFoundException e) { // Kullanıcı bulunamazsa
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @DeleteMapping("/cart")
    public ResponseEntity<Map<String, Boolean>> clearUserCart(Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            boolean isCleared = userService.clearUserCart(userId);
            return ResponseEntity.ok(Collections.singletonMap("isCleared", isCleared));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("isCleared", false));
        } catch (ResourceNotFoundException e) { // Kullanıcı bulunamazsa
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("isCleared", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("isCleared", false));
        }
    }


    //*********** order methods**********
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getUserOrders(Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            List<OrderDto> orders = orderService.getUserOrders(userId); // Assumes orderService.getUserOrders returns OrderDto
            return ResponseEntity.ok(orders);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(
            Authentication authentication,
            @RequestBody @Valid CreateOrderRequestDTO requestDTO
    ) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            OrderDto createdOrder = orderService.createOrder(userId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder); // Return 201 Created
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    //*********** address methods**********
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getUserAddresses(Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            List<AddressDto> addresses = userService.getUserAddresses(userId);
            return ResponseEntity.ok(addresses);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @PostMapping("/addresses")
    public ResponseEntity<ApplicationUser> addAddress(
            Authentication authentication,
            @RequestBody @Valid AddressDto addressDto
    ) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            ApplicationUser updatedUser = userService.addAddress(userId, addressDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser); // Return 201 Created
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ApplicationUser> updateAddress(
            @PathVariable String addressId,
            Authentication authentication,
            @RequestBody @Valid AddressDto addressDto
    ) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            ApplicationUser updatedUser = userService.updateAddress(userId, addressId, addressDto);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApplicationUser> deleteAddress(
            @PathVariable String addressId,
            Authentication authentication
    ) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            ApplicationUser updatedUser = userService.deleteAddress(userId, addressId);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    //*********** payment methods**********

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getUserPayments(Authentication authentication) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            List<PaymentDto> payments = userService.getUserPayments(userId);
            return ResponseEntity.ok(payments);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @PostMapping("/payments")
    public ResponseEntity<ApplicationUser> addPayment(
            Authentication authentication,
            @RequestBody @Valid PaymentRequestDto paymentDto
    ) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            ApplicationUser updatedUser = userService.addPayment(userId, paymentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<ApplicationUser> deletePayment(
            @PathVariable String paymentId,
            Authentication authentication
    ) {
        try {
            String userId = userService.getUserIdFromAuthentication(authentication);
            ApplicationUser updatedUser = userService.deletePayment(userId, paymentId);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}