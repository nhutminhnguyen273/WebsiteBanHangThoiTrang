package com.nhom15.fashion.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    private long totalPrice;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CartItem(Product product, int quantity, User user) {
        this.product = product;
        this.quantity = quantity;
        this.user = user;
        this.totalPrice = product.getPrice() * quantity;
    }
}
