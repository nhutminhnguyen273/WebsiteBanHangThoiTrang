package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser_Id(Long userId);
    Optional<CartItem> findByProduct_IdAndUser(Long productId, User user);
    void deleteByUser(User user);
}
