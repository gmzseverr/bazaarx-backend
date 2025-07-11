package com.bazaarx.bazaarxbackend.service.user;




import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;

import com.bazaarx.bazaarxbackend.repo.ProductRepository;
import com.bazaarx.bazaarxbackend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Spring, bu sınıfı UserService arayüzünün bir uygulaması olarak bulur
@RequiredArgsConstructor
public class UserServiceImpl implements UserService { // Arayüzü uygula

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public Optional<ApplicationUser> findUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<ApplicationUser> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional
    public boolean toggleFavoriteProduct(String userId, String productId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Ürünün gerçekten var olup olmadığını kontrol etmek iyi bir pratiktir.
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (user.getFavorites().contains(productId)) {
            // Ürün zaten favorilerdeyse, kaldır
            user.getFavorites().remove(productId);
            userRepository.save(user);
            return false; // Kaldırıldı
        } else {
            // Ürün favorilerde değilse, ekle
            user.getFavorites().add(productId);
            userRepository.save(user);
            return true; // Eklendi
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

}