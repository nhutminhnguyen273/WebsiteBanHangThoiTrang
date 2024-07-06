package com.nhom15.fashion.controllers;

import com.nhom15.fashion.Utils.Utils;
import com.nhom15.fashion.config.PaypalPaymentIntent;
import com.nhom15.fashion.config.PaypalPaymentMethod;
import com.nhom15.fashion.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaypalPaymentController {

    public static final String URL_PAYPAL_SUCCESS = "pay/success";
    public static final String URL_PAYPAL_CANCEL = "pay/cancel";
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private PaypalService paypalService;

    @GetMapping("/showPaymentPage")
    public String showPaymentPage(@RequestParam("orderId") String orderId, @RequestParam("totalPrice") double totalPrice, Model model){
        model.addAttribute("orderId", orderId);
        model.addAttribute("totalPrice", totalPrice);
        return "paymentpaypal/index";
    }

    @PostMapping("/pay")
    public String pay(@RequestParam("totalPrice") double price, @RequestParam("orderId") String orderId, HttpServletRequest request, RedirectAttributes redirectAttributes){
        String cancelUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL + "?orderId=" + orderId;
        String successUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS + "?orderId=" + orderId;
        try{
            Payment payment = paypalService.createPayment(
                    price,
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    "payment description",
                    cancelUrl,
                    successUrl);
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return "redirect:" +links.getHref();
                }
            }
        }catch (PayPalRESTException e){
            log.error(e.getMessage());
        }
        redirectAttributes.addAttribute("orderId", orderId);
        return "redirect:/order/confirmation";
    }

    @GetMapping(URL_PAYPAL_CANCEL)
    public String cancelPay(){
        return "paymentpaypal/cancel";
    }

    @GetMapping(URL_PAYPAL_SUCCESS)
    public String successPay(@RequestParam("paymentId") String paymnetId, @RequestParam("orderId") Long orderId, @RequestParam("PayerID") String payerId, RedirectAttributes redirectAttributes){
        try{
            Payment payment = paypalService.executePayment(paymnetId, payerId);
            if(payment.getState().equals("approved")){
                redirectAttributes.addAttribute("orderId", orderId);
                return "redirect:/order/confirmation";
            }
        }catch(PayPalRESTException e){
            log.error(e.getMessage());
        }
        return "redirect:/";
    }
}
