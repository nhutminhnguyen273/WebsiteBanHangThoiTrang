package com.nhom15.fashion.controllers;

import com.nhom15.fashion.config.MomoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.nhom15.fashion.config.MoMoSecurity;

import java.util.UUID;

@Controller
@RequestMapping("/payment")
public class PaymentMomoController {

    @Autowired
    private MomoConfig moMoConfig;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/initiate")
    public String showPaymentForm() {
        return "payment_form";
    }

    @PostMapping("/momo")
    public String initiatePayment(@RequestParam("amount") long amount, Model model) {
        try {
            String requestId = UUID.randomUUID().toString();
            String orderId = UUID.randomUUID().toString();
            String orderInfo = "Thanh toán phí mua hàng.";
            String extraData = "";

            MoMoPaymentRequest request = new MoMoPaymentRequest();
            request.setPartnerCode(moMoConfig.getPartnerCode());
            request.setAccessKey(moMoConfig.getAccessKey());
            request.setRequestId(requestId);
            request.setOrderId(orderId);
            request.setAmount(String.valueOf(amount));
            request.setOrderInfo(orderInfo);
            request.setReturnUrl(moMoConfig.getReturnUrl());
            request.setNotifyUrl(moMoConfig.getNotifyUrl());
            request.setRequestType("captureMoMoWallet");
            request.setExtraData(extraData);

            String rawSignature = "partnerCode=" + moMoConfig.getPartnerCode() + "&accessKey=" + moMoConfig.getAccessKey() + "&requestId=" + requestId + "&amount=" + amount + "&orderId=" + orderId + "&orderInfo=" + orderInfo + "&returnUrl=" + moMoConfig.getReturnUrl() + "&notifyUrl=" + moMoConfig.getNotifyUrl() + "&extraData=" + extraData;
            String signature = MoMoSecurity.signSHA256(rawSignature, moMoConfig.getSecretKey());
            request.setSignature(signature);

            MoMoPaymentResponse response = restTemplate.postForObject(moMoConfig.getEndpoint(), request, MoMoPaymentResponse.class);

            if (response != null) {
                return "redirect:" + response.getPayUrl();
            } else {
                model.addAttribute("error", "Đã xảy ra lỗi khi khởi tạo thanh toán với MoMo.");
                return "payment_form";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Đã xảy ra lỗi khi khởi tạo thanh toán với MoMo: " + e.getMessage());
            return "payment_form";
        }
    }

    @PostMapping("/notify")
    public void handlePaymentNotification(@RequestBody MoMoPaymentResponse response) {
        // Xử lý thông báo thanh toán từ MoMo (thông qua callback)
        if ("0".equals(response.getResultCode())) {
            // Thanh toán thành công, xử lý logic tại đây
            System.out.println("Payment successful for orderId: " + response.getOrderId());
        } else {
            // Thanh toán thất bại, xử lý logic tại đây
            System.out.println("Payment failed for orderId: " + response.getOrderId() + ", errorCode: " + response.getResultCode());
        }
    }

    @GetMapping("/confirm")
    public String confirmPayment(@RequestParam("orderId") String orderId, @RequestParam("resultCode") String resultCode, Model model) {
        if ("0".equals(resultCode)) {
            model.addAttribute("message", "Thanh toán thành công.");
        } else {
            model.addAttribute("error", "Đã xảy ra lỗi khi thanh toán: " + resultCode);
        }
        return "payment_result";
    }
}
