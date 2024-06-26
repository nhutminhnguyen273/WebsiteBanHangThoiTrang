package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.OrderDetails;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.service.CartService;
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
    public String checkout(){
        return "/cart/checkout";
    }

    @PostMapping("/pay")
    public String submitOrder(String customerName, String address, String email, String phone, String note, String paymentMethod, RedirectAttributes redirectAttributes) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        Long orderId = orderService.createdOrder(customerName, address, email, phone, note, cartItems);
        redirectAttributes.addAttribute("orderId", orderId);

        switch (paymentMethod) {
            case "vnp":
                return "redirect:/payment/create_payment?orderId=" + orderId;
            case "paypal":
                long totalPrice = cartItems.stream().mapToLong(item -> item.getTotalPrice() * item.getQuantity()).sum();
                double totalPriceInUSD = (double) totalPrice / 25451;
                redirectAttributes.addAttribute("totalPrice", totalPriceInUSD);
                return "redirect:/showPaymentPage?totalPrice=" + totalPriceInUSD + "&orderId=" + orderId;
            case "momo":
                totalPrice = cartItems.stream().mapToLong(item -> item.getTotalPrice() * item.getQuantity()).sum();
                redirectAttributes.addAttribute("totalPrice", totalPrice);
                return "redirect:/payment/initiate?amount=" + totalPrice + "&orderId=" + orderId;
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
    public String orderConfirmation(@RequestParam("orderId") Long orderId, Model model) {
        orderService.updateOrderStatus(orderId, true);
        List<OrderDetails> orderDetailsList = orderService.getOrderDetailsByOrderId(orderId);

        int totalQuantity = orderDetailsList.stream().mapToInt(OrderDetails::getQuantity).sum();
        long totalAmount = orderDetailsList.stream().mapToLong(orderDetails -> orderDetails.getProduct().getPrice() * orderDetails.getQuantity()).sum();

        orderService.saveInvoice(orderId, totalQuantity, totalAmount);

        productService.updateProductQuantities(orderDetailsList);

        cartService.clearCart(); // Clear the cart after successful order

        model.addAttribute("message", "Bạn đã đặt hàng thành công!");
        return "cart/order-confirmation";
    }

    @GetMapping("/failed")
    public String orderFailed(@RequestParam("orderId") Long orderId, Model model) {
        orderService.updateOrderStatus(orderId, false);
        model.addAttribute("message", "Bạn đã đặt hàng thất bại");
        return "cart/order-failed";
    }
}
