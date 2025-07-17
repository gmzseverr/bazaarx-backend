// UserServiceImpl.java
package com.bazaarx.bazaarxbackend.service.user;

import com.bazaarx.bazaarxbackend.dto.AddressDto;
import com.bazaarx.bazaarxbackend.dto.PaymentDto;
import com.bazaarx.bazaarxbackend.dto.PaymentRequestDto;
import com.bazaarx.bazaarxbackend.dto.ProductResponse;
import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.entity.user.Address;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import com.bazaarx.bazaarxbackend.entity.user.Payment;
import com.bazaarx.bazaarxbackend.exceptions.ResourceNotFoundException;
import com.bazaarx.bazaarxbackend.mapper.ProductMapper;
import com.bazaarx.bazaarxbackend.repo.ProductRepository;
import com.bazaarx.bazaarxbackend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public Optional<ApplicationUser> findUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<ApplicationUser> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public boolean addProductToFavorites(String userId, String productId) {
        log.info("Attempting to add product {} to favorites for userId: {}", productId, userId);

        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException("Product not found with ID: " + productId);
                });

        if (!user.getFavorites().contains(productId)) { // Check if already present
            user.getFavorites().add(productId);
            userRepository.save(user);
            log.info("Product {} added to favorites for user {}", productId, userId);
            return true;
        } else {
            log.info("Product {} was already in favorites for user {}", productId, userId);
            return true; // Still considered "liked"
        }
    }

    @Transactional
    public boolean removeProductFromFavorites(String userId, String productId) {
        log.info("Attempting to remove product {} from favorites for userId: {}", productId, userId);

        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        if (user.getFavorites().remove(productId)) { // If removal is successful (was present)
            userRepository.save(user);
            log.info("Product {} removed from favorites for user {}", productId, userId);
            return false;
        } else {
            log.info("Product {} was not in favorites for user {}", productId, userId);
            return false;
        }
    }


    public boolean isProductFavoriteForUser(String userId, String productId) {
        return userRepository.findById(userId)
                .map(user -> user.getFavorites().contains(productId))
                .orElse(false);
    }



    public List<Product> getUserFavoriteProducts(String userId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<String> favoriteProductIds = user.getFavorites();
        return productRepository.findAllById(favoriteProductIds);
    }

    public List<ProductResponse> getUserFavoriteProductResponses(String userId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<String> favoriteProductIds = user.getFavorites();
        return productRepository.findAllById(favoriteProductIds)
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated.");
        }

        if (authentication.getPrincipal() instanceof ApplicationUser) {
            ApplicationUser user = (ApplicationUser) authentication.getPrincipal();
            return user.getId();
        }

        String email = authentication.getName();
        log.warn("Authentication principal is not ApplicationUser. Attempting to find user by email: {}", email); // Uyarı logu
        return userRepository.findByEmail(email)
                .map(ApplicationUser::getId)
                .orElseThrow(() -> {
                    log.error("User ID not found for authenticated email: {}", email); // Hata logu
                    return new IllegalStateException("User ID not found for authenticated email: " + email);
                });
    }

    @Transactional
    public boolean addProductToCart(String userId, String productId) {
        log.info("Attempting to add product {} to cart for userId: {}", productId, userId);
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));


        if (!user.getCart().contains(productId)) {
            user.getCart().add(productId);
            userRepository.save(user);
            log.info("Product {} added to cart for user {}", productId, userId);
            return true;
        } else {
            log.info("Product {} was already in cart for user {}", productId, userId);
            return false;
        }
    }

    @Transactional
    public boolean removeProductFromCart(String userId, String productId) {
        log.info("Attempting to remove product {} from cart for userId: {}", productId, userId);
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getCart().remove(productId)) {
            userRepository.save(user);
            log.info("Product {} removed from cart for user {}", productId, userId);
            return true;
        } else {
            log.info("Product {} was not in cart for user {}", productId, userId);
            return false; // Not in cart
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getUserCartProducts(String userId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<String> cartProductIds = user.getCart();


        if (cartProductIds == null || cartProductIds.isEmpty()) {
            return Collections.emptyList();
        }


        return productRepository.findAllById(cartProductIds)
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean clearUserCart(String userId) {
        log.info("Attempting to clear cart for userId: {}", userId);
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!user.getCart().isEmpty()) {
            user.getCart().clear();
            userRepository.save(user);
            log.info("Cart cleared for user {}", userId);
            return true;
        } else {
            log.info("Cart was already empty for user {}", userId);
            return false;
        }
    }
    @Override
    public ApplicationUser addAddress(String userId, AddressDto addressDto) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getAddresses() == null) {
            user.setAddresses(new ArrayList<>());
        }

        Address newAddress = modelMapper.map(addressDto, Address.class);
        newAddress.setId(UUID.randomUUID().toString());

        user.getAddresses().add(newAddress);
        return userRepository.save(user);
    }

    @Override
    public ApplicationUser updateAddress(String userId, String addressId, AddressDto updatedAddressDto) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getAddresses() == null) {
            throw new ResourceNotFoundException("User has no addresses.");
        }

        Optional<Address> existingAddressOpt = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst();

        if (existingAddressOpt.isEmpty()) {
            throw new ResourceNotFoundException("Address not found with ID: " + addressId);
        }

        Address existingAddress = existingAddressOpt.get();
        modelMapper.map(updatedAddressDto, existingAddress);
        existingAddress.setId(addressId);

        return userRepository.save(user);
    }

    @Override
    public ApplicationUser deleteAddress(String userId, String addressId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            throw new ResourceNotFoundException("User has no addresses to delete.");
        }

        boolean removed = user.getAddresses().removeIf(addr -> addr.getId().equals(addressId));

        if (!removed) {
            throw new ResourceNotFoundException("Address not found with ID: " + addressId);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public ApplicationUser addPayment(String userId, PaymentRequestDto paymentRequestDto) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getPayments() == null) {
            user.setPayments(new ArrayList<>());
        }

        Payment newPayment = modelMapper.map(paymentRequestDto, Payment.class);
        newPayment.setId(UUID.randomUUID().toString());
        newPayment.setCreatedAt(new Date());


        if (paymentRequestDto.getCardNumber() != null && paymentRequestDto.getCardNumber().length() >= 4) {
            newPayment.setLastFourDigits(paymentRequestDto.getCardNumber().substring(paymentRequestDto.getCardNumber().length() - 4));
        } else {
            newPayment.setLastFourDigits("N/A"); // Handle short numbers
        }
        newPayment.setTokenizedCardNumber("**** **** **** " + newPayment.getLastFourDigits());



        user.getPayments().add(newPayment);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getUserPayments(String userId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getPayments() == null || user.getPayments().isEmpty()) {
            return Collections.emptyList();
        }

        return user.getPayments().stream()
                .map(payment -> modelMapper.map(payment, PaymentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationUser deletePayment(String userId, String paymentId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getPayments() == null || user.getPayments().isEmpty()) {
            throw new ResourceNotFoundException("User has no payment methods to delete.");
        }

        boolean removed = user.getPayments().removeIf(pm -> pm.getId().equals(paymentId));

        if (!removed) {
            throw new ResourceNotFoundException("Payment method not found with ID: " + paymentId);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getUserAddresses(String userId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            return Collections.emptyList();
        }


        return user.getAddresses().stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .collect(Collectors.toList());
    }


}