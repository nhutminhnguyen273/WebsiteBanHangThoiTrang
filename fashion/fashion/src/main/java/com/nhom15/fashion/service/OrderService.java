package com.nhom15.fashion.service;

import com.nhom15.fashion.models.*;
import com.nhom15.fashion.repositories.InvoiceRepository;
import com.nhom15.fashion.repositories.OrderDetailRepository;
import com.nhom15.fashion.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional
    public String createdOrder(String customerName, String address, String email, String phone, String note, List<CartItem> cartItems, Long userId){
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setAddress(address);
        order.setEmail(email);
        order.setPhone(phone);
        order.setNote(note);
        order.setStatus(false);
        order.setCreatedDate(LocalDate.now());
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderDetails details = new OrderDetails();
            details.setOrder(order);
            details.setProduct(item.getProduct());
            details.setQuantity(item.getQuantity());
            details.setSize(item.getSize());
            orderDetailRepository.save(details);
        }

        double totalCost = calculateTotalPrice(cartItems);
        order.setTotalPrice(totalCost);
        orderRepository.save(order);

        cartService.clearCart(userId);
        return order.getId();
    }


    @Transactional
    private double calculateTotalPrice(List<CartItem> cartItems) {
        double totalCost = 0;
        for (CartItem item : cartItems) {
            totalCost += item.getProduct().getPrice() * item.getQuantity();
        }
        return totalCost;
    }

    @Transactional
    public BigDecimal calculateTotalPrice(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy sản phẩm.");
        }
        Order order = orderOpt.get();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderDetails details : order.getOrderDetails()) {
            Product product = details.getProduct();
            if (product != null && product.getId() != null) {
                BigDecimal price = BigDecimal.valueOf(product.getPrice());
                totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(details.getQuantity())));
            }
        }
        return totalPrice;
    }
    @Transactional
    public void updateOrderStatus(String orderId, boolean status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }
    @Transactional
    public void saveInvoice(String orderId, int quantity, long totalAmount) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();

            Invoice invoice = new Invoice();
            invoice.setCreatedDate(LocalDate.now());
            invoice.setQuantity(quantity);
            invoice.setTotalAmount(totalAmount);
            invoice.setOrder(order);
            invoice.setUser(user);

            invoiceRepository.save(invoice);

            order.setInvoice(invoice);
            orderRepository.save(order);

        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

            User user = userService.processOAuthPostLogin(oauth2User);

            Invoice invoice = new Invoice();
            invoice.setCreatedDate(LocalDate.now());
            invoice.setQuantity(quantity);
            invoice.setTotalAmount(totalAmount);
            invoice.setOrder(order);
            invoice.setUser(user);

            invoiceRepository.save(invoice);

            order.setInvoice(invoice);
            orderRepository.save(order);

        } else {
            throw new IllegalStateException("Unexpected authentication principal type: " + authentication.getPrincipal().getClass());
        }
    }
    @Transactional(readOnly = true)
    public List<OrderDetails> getOrderDetailsByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
