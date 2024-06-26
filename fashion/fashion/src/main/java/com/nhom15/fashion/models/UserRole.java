package com.nhom15.fashion.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {
    ADMIN(1),
    CUSTOMER(2),
    EMPLOYER(3);
    public final long value;
}
