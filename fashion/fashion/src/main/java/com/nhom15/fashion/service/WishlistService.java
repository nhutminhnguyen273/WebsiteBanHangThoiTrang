package com.nhom15.fashion.service;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.WishlistItem;
import com.nhom15.fashion.repositories.IUserRepository;
import com.nhom15.fashion.repositories.ProductRepository;
import com.nhom15.fashion.repositories.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    @Autowired
    private WishlistItemRepository wishlistItemRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        Optional<WishlistItem> existingItem = wishlistItemRepository.findByProduct_IdAndUser_Id(userId, productId);

        if (existingItem.isPresent()) {
            return;
        }
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setUser(user);
        wishlistItem.setProduct(product);
        wishlistItemRepository.save(wishlistItem);
    }

    @Transactional
    public void removeFromWishlist(Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found with id: " + wishlistItemId));

        wishlistItemRepository.delete(wishlistItem);
    }

    @Transactional(readOnly = true)
    public List<WishlistItem> getWishlistByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return wishlistItemRepository.findByUserId(userId);
    }
}
