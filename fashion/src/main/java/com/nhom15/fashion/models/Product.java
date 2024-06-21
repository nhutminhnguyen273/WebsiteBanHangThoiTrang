package com.nhom15.fashion.models;

import com.nhom15.fashion.validator.annotation.ValidUserId;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 250, nullable = false)
    @NotBlank(message = "Tên sản phẩm không được để trống.")
    private String name;

    private LocalDate createdDate;

    private String description;

    private String avatar;

    @Min(value = 0, message = "Số lượng sản phẩm phải là số không âm.")
    private int quantity;

    @Min(value = 0, message = "Giá sản phẩm phải là số không âm.")
    private double price;

    @Builder.Default
    private boolean hide = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ValidUserId
    private User user;
}
