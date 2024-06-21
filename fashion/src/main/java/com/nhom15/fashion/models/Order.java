package com.nhom15.fashion.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private String address;

    private String email;

    private String phone;

    @OneToMany(mappedBy = "order")
    private List<OrderDetails> orderDetails;
}
