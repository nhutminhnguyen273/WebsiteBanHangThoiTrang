package com.nhom15.fashion.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    private String size;

    private long totalPrice;

    @Transient
    private String formattedPrice;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CartItem(Product product, int quantity, String size, User user) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.user = user;
        this.totalPrice = product.getPrice() * quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        this.totalPrice = product.getPrice() * quantity;
    }

}
