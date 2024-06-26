package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public String showCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        // Tính tổng chi phí của các mặt hàng trong giỏ hàng
        long totalCost = cartItems.stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("totalCost", totalCost);
        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity){
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id){
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(){
        cartService.clearCart();
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("quantities") int[] quantities, @RequestParam("productIds") Long[] productIds, RedirectAttributes redirectAttributes) {
        Map<Long, Integer> productQuantities = new HashMap<>();
        for (int i = 0; i < productIds.length; i++) {
            productQuantities.put(productIds[i], quantities[i]);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        //cartService.updateCart(userId, productQuantities);

        redirectAttributes.addFlashAttribute("message", "Giỏ hàng đã được cập nhật thành công");
        return "redirect:/cart";
    }
}
