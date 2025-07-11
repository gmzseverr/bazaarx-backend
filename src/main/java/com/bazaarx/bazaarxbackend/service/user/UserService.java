package com.bazaarx.bazaarxbackend.service.user;



import com.bazaarx.bazaarxbackend.entity.Product;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<ApplicationUser> findUserById(String id);
    Optional<ApplicationUser> findUserByEmail(String email);
   boolean toggleFavoriteProduct(String userId, String productId);
    boolean isProductFavoriteForUser(String userId, String productId);
    List<Product> getUserFavoriteProducts(String userId);

}