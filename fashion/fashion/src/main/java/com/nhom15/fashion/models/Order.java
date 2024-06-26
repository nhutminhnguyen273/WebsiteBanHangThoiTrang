package com.nhom15.fashion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
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

    @Column(name = "email", length = 50)
    @Email
    private String email;

    @Column(name = "phone", length = 10)
    @Length(min = 10, max = 10, message = "Số điện thoại phải có 10 số.")
    private String phone;

    private String note;

    private LocalDate createdDate;

    private double totalPrice;

    private boolean status;

    @OneToMany(mappedBy = "order")
    private List<OrderDetails> orderDetails;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Invoice invoice;
}
