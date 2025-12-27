package com.Crud.Crud.Service;


import com.Crud.Crud.Entity.*;
import com.Crud.Crud.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final Userrepo userRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;

    @Transactional
    public Long confirmPaymentAndCreateOrder(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.CREATED &&
                payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new RuntimeException("Invalid payment state");
        }

        // 🔥 PAYMENT SUCCESS
        payment.setStatus(PaymentStatus.SUCCESS);

        User user = payment.getUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setPayment(payment);
        order.setTotalAmount(payment.getAmount());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }

        // 🔥 Clear cart AFTER order creation
        cartRepository.deleteByUser(user);

        return savedOrder.getId();
    }
}
