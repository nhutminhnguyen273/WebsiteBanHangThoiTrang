package com.nhom15.fashion.service;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.WishlistItem;
import com.nhom15.fashion.repositories.IUserRepository;
import com.nhom15.fashion.repositories.ProductRepository;
import com.nhom15.fashion.repositories.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    @Autowired
    private WishlistItemRepository wishlistItemRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public List<WishlistItem> getWishlistItems(Long userId) {
        return wishlistItemRepository.findByUserId(userId);
    }

    @Transactional
    public void addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng này"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm này"));

        Optional<WishlistItem> existingWishlistItem = wishlistItemRepository.findByUserAndProduct(user, product);

        if (existingWishlistItem.isPresent()) {
            throw new IllegalStateException("Sản phẩm đã có trong danh sách yêu thích của bạn");
        } else {
            WishlistItem wishlistItem = new WishlistItem(user, product);
            wishlistItemRepository.save(wishlistItem);
        }
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm này trong danh sách yêu thích"));

        if (!wishlistItem.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền thực hiện thao tác này");
        }
        wishlistItemRepository.delete(wishlistItem);
    }
}
