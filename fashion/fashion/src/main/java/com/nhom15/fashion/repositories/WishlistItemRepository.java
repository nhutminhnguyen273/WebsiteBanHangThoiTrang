package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUser(User user);
    Optional<WishlistItem> findByUserAndProduct(User user, Product product);
    List<WishlistItem> findByUserId(Long userId);
}
