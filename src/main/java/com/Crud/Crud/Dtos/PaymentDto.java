package com.Crud.Crud.Dtos;

import com.Crud.Crud.Entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDto {
    private Long id;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private double amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
}
