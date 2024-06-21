package com.nhom15.fashion.models;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;
}
