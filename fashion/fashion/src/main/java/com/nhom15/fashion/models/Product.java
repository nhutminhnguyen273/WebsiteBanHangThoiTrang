package com.nhom15.fashion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "TEXT")
    private String description;

    private String avatar;

    @Min(value = 0, message = "Số lượng sản phẩm phải là số không âm.")
    private int quantity;

    @Min(value = 0, message = "Giá sản phẩm phải là số không âm.")
    private long price;

    @Builder.Default
    private boolean hide = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetails> details = new ArrayList<>();

}
