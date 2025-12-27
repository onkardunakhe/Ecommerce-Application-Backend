package com.Crud.Crud.Controllers;

import com.Crud.Crud.Dtos.PaymentDto;
import com.Crud.Crud.Entity.Payment;
import com.Crud.Crud.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {


    private final PaymentService paymentService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPayment(Authentication authentication) {
        System.out.println("Creating payment for user: " + authentication.getName());
        PaymentDto payment = paymentService.createPayment(authentication.getName());
        return ResponseEntity.ok(payment);
    }
}
