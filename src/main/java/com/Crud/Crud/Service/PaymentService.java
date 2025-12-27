package com.Crud.Crud.Service;

import com.Crud.Crud.Dtos.PaymentDto;
import com.Crud.Crud.Entity.CartItem;
import com.Crud.Crud.Entity.Payment;
import com.Crud.Crud.Entity.PaymentStatus;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.CartItemRepository;
import com.Crud.Crud.Repository.CartRepository;
import com.Crud.Crud.Repository.PaymentRepository;
import com.Crud.Crud.Repository.Userrepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;
    private final Userrepo userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PaymentDto createPayment(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreatedAt(LocalDateTime.now());

        return modelMapper.map(paymentRepository.save(payment), PaymentDto.class);

    }
}


