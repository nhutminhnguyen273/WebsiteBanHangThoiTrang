package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.Voucher;
import com.nhom15.fashion.service.CartService;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public String showCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "users/login";
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            return "redirect:/error";
        }

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        List<CartItem> cartItems = cartService.getCartItems(userId);
        DecimalFormat decimalFormat = new DecimalFormat("#,### VND");

        cartItems.forEach(item -> {
            item.setFormattedPrice(decimalFormat.format(item.getProduct().getPrice() * item.getQuantity()));
        });

        long totalCost = cartItems.stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        String formattedTotalCost = decimalFormat.format(totalCost);

        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalCost", formattedTotalCost);

        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            @RequestParam(name = "size", required = false) String size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        if (!(auth.getPrincipal() instanceof User)) {
            return "redirect:/error";
        }
        Long userId = ((User) auth.getPrincipal()).getId();
        if (productId == null || quantity <= 0) {
            return "redirect:/error";
        }
        cartService.addToCart(userId, productId, quantity, size);
        return "redirect:/cart";
    }


    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        Long userId = ((User) auth.getPrincipal()).getId();
        cartService.removeFromCart(userId, id);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        Long userId = ((User) auth.getPrincipal()).getId();
        cartService.clearCart(userId);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("quantities") int[] quantities,
                             @RequestParam("productIds") Long[] productIds,
                             RedirectAttributes redirectAttributes) {
        if (quantities == null || productIds == null || quantities.length != productIds.length) {
            throw new IllegalArgumentException("Invalid quantities or productIds");
        }

        Map<Long, Integer> productQuantities = new HashMap<>();
        for (int i = 0; i < productIds.length; i++) {
            productQuantities.put(productIds[i], quantities[i]);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Long userId = ((User) authentication.getPrincipal()).getId();

        cartService.updateCart(userId, productQuantities);

        redirectAttributes.addFlashAttribute("message", "Giỏ hàng đã được cập nhật thành công");
        return "redirect:/cart";
    }
}
