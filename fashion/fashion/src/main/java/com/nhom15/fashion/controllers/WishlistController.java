package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.WishlistItem;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String viewWishlist(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            return "redirect:/error";
        }

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        List<WishlistItem> wishlistItems = wishlistService.getWishlistByUserId(userId);
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);
        model.addAttribute("wishlistItems", wishlistItems);
        return "wishlist/wishlist";
    }

    @PostMapping("/add")
    public String addToWishlist(@RequestParam("productId") Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        if (!(authentication.getPrincipal() instanceof User)) {
            return "redirect:/error";
        }
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        wishlistService.addToWishlist(userId, productId);
        return "redirect:/wishlist";
    }

    @PostMapping("/remove/{wishlistItemId}")
    public String removeFromWishlist(@PathVariable("wishlistItemId") Long wishlistItemId) {
        wishlistService.removeFromWishlist(wishlistItemId);
        return "redirect:/wishlist";
    }
}