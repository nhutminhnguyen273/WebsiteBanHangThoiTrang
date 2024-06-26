package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
