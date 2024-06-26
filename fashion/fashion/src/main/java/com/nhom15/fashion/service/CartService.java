package com.nhom15.fashion.service;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.repositories.CartRepository;
import com.nhom15.fashion.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;

@Service
@SessionScope
@Getter
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public List<CartItem> getCartItems() {
        return cartRepository.findAll();
    }

    @Transactional
    public void addToCart(Long productId, int quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        User user = null;
        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            user = userService.processOAuthPostLogin(oauth2User);
        } else {
            throw new IllegalStateException("Unsupported user authentication type");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm này"));

        Optional<CartItem> existingCartItem = cartRepository.findByProduct_IdAndUser(productId, user);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(product, quantity, user);
            cartRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(Long id) {
        if(!cartRepository.existsById(id))
            throw new IllegalStateException("Không tìm thấy sản phẩm này.");
        cartRepository.deleteById(id);
    }

    @Transactional
    public void clearCart() {
        cartRepository.deleteAll();
    }

    @Transactional
    public void updateCart(Map<Long, Integer> productQuantities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        User user = null;
        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            user = userService.processOAuthPostLogin(oauth2User);
        } else {
            throw new IllegalStateException("Unsupported user authentication type");
        }
        List<CartItem> cartItems = cartRepository.findAll();

        if (cartItems.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy sản phẩm nào trong giỏ hàng");
        }

        for (CartItem cartItem : cartItems) {
            Long productId = cartItem.getProduct().getId();
            if (productQuantities.containsKey(productId)) {
                int newQuantity = productQuantities.get(productId);
                if (cartItem.getQuantity() != newQuantity) {
                    cartItem.setQuantity(newQuantity);
                    cartRepository.save(cartItem);
                }
            }
        }
    }

    @Transactional
    public double calculateTotalCost(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }
}