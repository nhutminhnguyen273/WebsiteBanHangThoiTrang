package com.nhom15.fashion.service;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.Order;
import com.nhom15.fashion.models.OrderDetails;
import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.repositories.OrderDetailRepository;
import com.nhom15.fashion.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

/*    @Transactional
    public Order createOrder(String customerName, String address, String phone, List<CartItem> cartItems){
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setAddress(address);
        order.setPhone(phone);
    }*/

    public BigDecimal calculateTotalPrice(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new EntityNotFoundException("Order not found");
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
}
