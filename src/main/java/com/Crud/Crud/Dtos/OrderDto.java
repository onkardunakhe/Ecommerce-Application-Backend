package com.Crud.Crud.Dtos;

import com.Crud.Crud.Entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class OrderDto {

    private Long id;
    private double totalAmount;
    private OrderStatus status;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private PaymentDto payment;
}
