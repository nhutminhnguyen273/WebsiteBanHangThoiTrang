package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {
}
