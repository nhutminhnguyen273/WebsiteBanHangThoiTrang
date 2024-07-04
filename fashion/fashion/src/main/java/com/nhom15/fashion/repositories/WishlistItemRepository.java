package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserId(Long userId);
    WishlistItem findByUserIdAndProductId(Long userId, Long productId);
    Optional<WishlistItem> findByProduct_IdAndUser_Id(Long userId, Long productId);
}
