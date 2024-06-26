package com.nhom15.fashion.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promotion")
public class Voucher {
    @Id
    private String id;

    private String name;

    private LocalDate createdDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private float discount;

    private int quantity;
}
