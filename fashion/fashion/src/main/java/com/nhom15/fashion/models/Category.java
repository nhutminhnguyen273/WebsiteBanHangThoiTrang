package com.nhom15.fashion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    @Length(max = 50, message = "Tên loại không quá 50 ký tự.")
    @NotBlank(message = "Tên loại không được để trống.")
    private String name;

    @Builder.Default
    private boolean hide = false;
}
