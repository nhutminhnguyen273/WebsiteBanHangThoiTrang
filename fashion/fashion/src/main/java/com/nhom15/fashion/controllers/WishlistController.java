package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.WishlistItem;
import com.nhom15.fashion.service.UserService;
import com.nhom15.fashion.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/add")
    public String addToWishlist(@RequestParam("productId") Long productId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        if (!(authentication.getPrincipal() instanceof User)) {
            return "redirect:/error";
        }
        Long userId = ((User) authentication.getPrincipal()).getId();
        wishlistService.addToWishlist(userId, productId);

        return "redirect:/wishlist";
    }

    @PostMapping("/remove/{id}")
    public String removeFromWishlist(@PathVariable("id") Long wishlistItemId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            return "redirect:/error";
        }

        Long userId = ((User) authentication.getPrincipal()).getId();

        // Remove item from wishlist
        wishlistService.removeFromWishlist(userId, wishlistItemId);

        return "redirect:/wishlist";
    }
    @GetMapping
    public String showWishlist(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            return "redirect:/error";
        }

        Long userId = ((User) authentication.getPrincipal()).getId();

        List<WishlistItem> wishlistItems = wishlistService.getWishlistItems(userId);

        model.addAttribute("wishlistItems", wishlistItems);

        return "wishlist/wishlist";
    }
}