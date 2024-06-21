package com.nhom15.fashion.service;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.repositories.ProductRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    @Getter
    private List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    public void addToCart(Long productId, int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm này"));
        cartItems.add(new CartItem(product, quantity));
    }

    public void removeFromCart(Long productId){
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clearCart(){
        cartItems.clear();
    }
}
