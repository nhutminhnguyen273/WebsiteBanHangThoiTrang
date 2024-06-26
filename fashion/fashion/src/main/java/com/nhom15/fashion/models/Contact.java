package com.nhom15.fashion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống.")
    private String customerName;

    @NotBlank(message = "Tiêu đề không được để trống.")
    private String title;

    @NotBlank(message = "Nội dung không được để trống.")
    private String content;

    @NotBlank(message = "Email không được trống.")
    @Email
    private String email;

    @Pattern(regexp = "^[0-9]*$", message = "Số điện thoại phải là số.")
    @Length(min = 10, max = 10, message = "Số điện thoại phải có 10 số.")
    private String phone;

    private LocalDate createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
