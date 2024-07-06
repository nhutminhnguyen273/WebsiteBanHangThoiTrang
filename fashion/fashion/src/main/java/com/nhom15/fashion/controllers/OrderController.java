package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.*;
import com.nhom15.fashion.service.CartService;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.OrderService;
import com.nhom15.fashion.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/update")
    public String updateCart(@RequestParam("quantities") int[] quantities, @RequestParam("productIds") Long[] productIds, RedirectAttributes redirectAttributes) {
        Map<Long, Integer> productQuantities = new HashMap<>();
        for (int i = 0; i < productIds.length; i++) {
            productQuantities.put(productIds[i], quantities[i]);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Long userId = userDetails.getId();

        //cartService.updateCart(userId, productQuantities);

        redirectAttributes.addFlashAttribute("message", "Giỏ hàng đã được cập nhật thành công");
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model){
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);
        return "/cart/checkout";
    }
    @PostMapping("/pay")
    public String submitOrder(String customerName, String address, String email, String phone, String note, String paymentMethod, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        Long userId = ((User) auth.getPrincipal()).getId();

        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        long totalPrice = cartItems.stream().mapToLong(item -> item.getTotalPrice()).sum();

        String orderId = orderService.createdOrder(customerName, address, email, phone, note, cartItems, userId);
        redirectAttributes.addAttribute("orderId", orderId);

        switch (paymentMethod) {
            case "vnp":
                return "redirect:/payment/create_payment";
            case "paypal":
                double totalPriceInUSD = (double) totalPrice / 25451;
                redirectAttributes.addAttribute("totalPrice", totalPriceInUSD);
                return "redirect:/showPaymentPage"; // Redirect to show PayPal payment page
            case "momo":
                // Redirect to MoMo payment initiation with total price
                redirectAttributes.addAttribute("totalPrice", totalPrice);
                return "redirect:/payment/initiate";
            default:
                return "redirect:/cart";
        }
    }

/*    @PostMapping("/vnp")
    public String payVNP(String customerName, String address, String email, String phone, String note, RedirectAttributes redirectAttributes) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        Long orderId = orderService.createdOrder(customerName, address, email, phone, note, cartItems);
        redirectAttributes.addAttribute("orderId", orderId);
        return "redirect:/payment/create_payment?orderId=" + orderId;
    }

    @PostMapping("/pay-pal")
    public String payPal(String customerName, String address, String email, String phone, String note, RedirectAttributes redirectAttributes) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        Long orderId = orderService.createdOrder(customerName, address, email, phone, note, cartItems);
        Long totalPrice = cartItems.stream().mapToLong(item -> item.getTotalPrice() * item.getQuantity()).sum();
        double totalPriceInUSD = (double) totalPrice / 25451;
        redirectAttributes.addAttribute("totalPrice", totalPriceInUSD);
        return "redirect:/showPaymentPage?totalPrice=" + (totalPriceInUSD);
    }

    @PostMapping("/momo")
    public String payMoMo(String customerName, String address, String email, String phone, String note, RedirectAttributes redirectAttributes) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        Long orderId = orderService.createdOrder(customerName, address, email, phone, note, cartItems);
        Long totalPrice = cartItems.stream().mapToLong(item -> item.getTotalPrice() * item.getQuantity()).sum();
        redirectAttributes.addAttribute("totalPrice", totalPrice);
        return "redirect:/payment/initiate?amount=" + totalPrice;
    }*/

    @GetMapping("/confirmation")
    public String paymentConfirmation(@RequestParam Map<String, String> allParams, @RequestParam("orderId") String orderId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        Long userId = ((User) auth.getPrincipal()).getId();

        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);

        if ("00".equals(vnp_ResponseCode)) {
            orderService.updateOrderStatus(orderId, true);
            List<OrderDetails> orderDetailsList = orderService.getOrderDetailsByOrderId(orderId);

            int totalQuantity = orderDetailsList.stream().mapToInt(OrderDetails::getQuantity).sum();
            long totalAmount = orderDetailsList.stream().mapToLong(orderDetails -> {
                long price = orderDetails.getProduct().getPrice();
                Voucher voucher = orderDetails.getOrder().getVoucher();
                if (voucher != null) {
                    long discount = price * voucher.getDiscount() / 100;
                    price -= discount;
                }
                return price * orderDetails.getQuantity();
            }).sum();

            orderService.saveInvoice(orderId, totalQuantity, totalAmount);

            productService.updateProductQuantities(orderDetailsList);

            cartService.clearCart(userId);

            model.addAttribute("message", "Bạn đã đặt hàng thành công!");
            return "cart/order-confirmation";
        } else {
            orderService.updateOrderStatus(orderId, false);
            model.addAttribute("message", "Bạn đã đặt hàng thất bại");
            return "cart/order-failed";
        }
    }



    @GetMapping("/failed")
    public String orderFailed(@RequestParam("orderId") String orderId, Model model) {
        orderService.updateOrderStatus(orderId, false);
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);
        model.addAttribute("message", "Bạn đã đặt hàng thất bại");
        return "cart/order-failed";
    }
}
