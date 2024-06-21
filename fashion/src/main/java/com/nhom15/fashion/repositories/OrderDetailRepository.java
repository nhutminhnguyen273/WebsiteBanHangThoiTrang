package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {
}
