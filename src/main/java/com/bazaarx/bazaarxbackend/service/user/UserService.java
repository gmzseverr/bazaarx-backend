package com.bazaarx.bazaarxbackend.service.user;



import com.bazaarx.bazaarxbackend.dto.AddressDto;
import com.bazaarx.bazaarxbackend.dto.PaymentDto;
import com.bazaarx.bazaarxbackend.dto.PaymentRequestDto;
import com.bazaarx.bazaarxbackend.dto.ProductResponse;
import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<ApplicationUser> findUserById(String id);
    Optional<ApplicationUser> findUserByEmail(String email);
    boolean isProductFavoriteForUser(String userId, String productId);
    boolean addProductToFavorites(String userId, String productId);
    boolean removeProductFromFavorites(String userId, String productId);
    List<ProductResponse> getUserFavoriteProductResponses(String userId);
    String getUserIdFromAuthentication(Authentication authentication);
    boolean addProductToCart(String userId, String productId);
  boolean removeProductFromCart(String userId, String productId);
    List<ProductResponse> getUserCartProducts(String userId);
    boolean clearUserCart(String userId);

    // Address
    ApplicationUser addAddress(String userId, AddressDto addressDto);
    ApplicationUser updateAddress(String userId, String addressId, AddressDto updatedAddressDto);
    ApplicationUser deleteAddress(String userId, String addressId);

    // Payment
    ApplicationUser addPayment(String userId, PaymentRequestDto paymentRequestDto);
    ApplicationUser deletePayment(String userId, String paymentId);

    List<AddressDto> getUserAddresses(String userId);

    List<PaymentDto> getUserPayments(String userId);
}