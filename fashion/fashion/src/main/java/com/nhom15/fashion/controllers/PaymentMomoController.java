package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.models.OrderDetails;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.orders.MoMoSecurity;
import com.nhom15.fashion.config.MomoConfig;
import com.nhom15.fashion.repositories.CategoryRepository;
import com.nhom15.fashion.service.CartService;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.OrderService;
import com.nhom15.fashion.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/payment")
public class PaymentMomoController {

    @Autowired
    private MomoConfig moMoConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/initiate")
    public String showPaymentForm(@RequestParam("orderId") String orderId, @RequestParam("totalPrice") long amount, Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "momo/payment_form";
    }

    @PostMapping("/momo")
    public String initiatePayment(@RequestParam("orderId") String orderId, @RequestParam("amount") long amount, Model model) {
        try {
            String requestId = UUID.randomUUID().toString();
            String momoOrderId = orderId;
            String orderInfo = "Thanh toán phí mua hàng.";
            String extraData = "";

            MoMoPaymentRequest request = new MoMoPaymentRequest();
            request.setPartnerCode(moMoConfig.getPartnerCode());
            request.setAccessKey(moMoConfig.getAccessKey());
            request.setRequestId(requestId);
            request.setOrderId(momoOrderId);
            request.setAmount(String.valueOf(amount));
            request.setOrderInfo(orderInfo);
            request.setReturnUrl(moMoConfig.getReturnUrl());
            request.setNotifyUrl(moMoConfig.getNotifyUrl());
            request.setRequestType("captureMoMoWallet");
            request.setExtraData(extraData);

            String rawSignature = "partnerCode=" + moMoConfig.getPartnerCode() + "&accessKey=" + moMoConfig.getAccessKey() + "&requestId=" + requestId + "&amount=" + amount + "&orderId=" + momoOrderId + "&orderInfo=" + orderInfo + "&returnUrl=" + moMoConfig.getReturnUrl() + "&notifyUrl=" + moMoConfig.getNotifyUrl() + "&extraData=" + extraData;
            String signature = MoMoSecurity.signSHA256(rawSignature, moMoConfig.getSecretKey());
            request.setSignature(signature);

            MoMoPaymentResponse response = restTemplate.postForObject(moMoConfig.getEndpoint(), request, MoMoPaymentResponse.class);

            if (response != null) {
                return "redirect:" + response.getPayUrl();
            } else {
                model.addAttribute("error", "Đã xảy ra lỗi khi khởi tạo thanh toán với MoMo.");
                return "momo/payment_form";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Đã xảy ra lỗi khi khởi tạo thanh toán với MoMo: " + e.getMessage());
            return "momo/payment_form";
        }
    }

    @PostMapping("/notify")
    public void handlePaymentNotification(@RequestBody MoMoPaymentResponse response) {
        // Xử lý thông báo thanh toán từ MoMo (thông qua callback)
        if ("0".equals(response.getResultCode())) {
            // Thanh toán thành công
            String returnUrl = moMoConfig.getReturnUrl() + "?resultCode=0"; // Thêm resultCode vào returnUrl
            // Redirect hoặc xử lý tiếp theo
        } else {
            // Thanh toán thất bại
            String returnUrl = moMoConfig.getReturnUrl() + "?resultCode=1"; // Thêm resultCode vào returnUrl
            // Redirect hoặc xử lý tiếp theo
        }
    }

    @GetMapping("/momo-confirmation")
    public String confirmPayment(@RequestParam("orderId") String orderId,
                                 @RequestParam(value = "resultCode", defaultValue = "1") String resultCode,
                                 Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        Long userId = ((User) auth.getPrincipal()).getId();
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);

        if ("0".equals(resultCode)) {
            orderService.updateOrderStatus(orderId, true);
            List<OrderDetails> orderDetailsList = orderService.getOrderDetailsByOrderId(orderId);

            int totalQuantity = orderDetailsList.stream().mapToInt(OrderDetails::getQuantity).sum();
            long totalAmount = orderDetailsList.stream().mapToLong(orderDetails -> orderDetails.getProduct().getPrice() * orderDetails.getQuantity()).sum();

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
}
