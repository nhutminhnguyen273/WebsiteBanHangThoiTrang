package com.nhom15.fashion.service;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.Voucher;
import com.nhom15.fashion.repositories.CartRepository;
import com.nhom15.fashion.repositories.IUserRepository;
import com.nhom15.fashion.repositories.ProductRepository;
import com.nhom15.fashion.repositories.VoucherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
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
    private IUserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    public List<CartItem> getCartItems(Long userId) {
        return cartRepository.findByUser_Id(userId);
    }

    @Transactional
    public void addToCart(Long userId, Long productId, int quantity, String size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng này"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm này"));

        Optional<CartItem> existingCartItem = cartRepository.findByProduct_IdAndUser(productId, user);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(product, quantity, size, user);
            cartRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm này"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Unauthorized operation");
        }

        cartRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng này"));

        cartRepository.deleteByUser(user);
    }

    @Transactional
    public void updateCart(Long userId, Map<Long, Integer> productQuantities) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng này"));

        List<CartItem> cartItems = cartRepository.findByUser_Id(userId);

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

    public double calculateTotalCost(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }
}