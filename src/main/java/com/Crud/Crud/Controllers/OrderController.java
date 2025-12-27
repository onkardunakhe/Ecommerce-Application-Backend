package com.Crud.Crud.Controllers;

import com.Crud.Crud.Dtos.OrderDto;
import com.Crud.Crud.Dtos.OrderItemDto;
import com.Crud.Crud.Dtos.PaymentDto;
import com.Crud.Crud.Dtos.UserDto;
import com.Crud.Crud.Entity.Order;
import com.Crud.Crud.Entity.OrderStatus;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.OrderItemRepository;
import com.Crud.Crud.Repository.OrderRepository;
import com.Crud.Crud.Repository.Userrepo;
import com.Crud.Crud.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final Userrepo userRepository;
    private final ModelMapper modelMapper;

    @PostMapping("/confirm-payment/{paymentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> confirmPayment(@PathVariable Long paymentId) {

        Long orderId = orderService.confirmPaymentAndCreateOrder(paymentId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Order placed successfully",
                        "orderId", orderId
                )
        );
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public List<OrderDto> myOrders(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser(user)
                .stream()
                .map(order -> {
                    OrderDto dto = modelMapper.map(order, OrderDto.class);

                    // 👇 ADD ORDER ITEMS MAPPING
                    dto.setItems(
                            order.getOrderItems().stream()
                                    .map(item -> {
                                        OrderItemDto itemDto = new OrderItemDto();
                                        itemDto.setId(item.getId());
                                        itemDto.setProductId(Long.valueOf(item.getProduct().getId()));
                                        itemDto.setProductName(item.getProduct().getName());
                                        itemDto.setPrice(item.getPrice());
                                        itemDto.setQuantity(item.getQuantity());
                                        return itemDto;
                                    })
                                    .collect(Collectors.toList())
                    );

                    // 👇 PAYMENT (already working for you)
                    if (order.getPayment() != null) {
                        dto.setPayment(modelMapper.map(order.getPayment(), PaymentDto.class));
                    }

                    return dto;
                })
                .collect(Collectors.toList());

    }


    @GetMapping("/getbyoid/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public OrderDto getOrder(@PathVariable Long orderId, Authentication authentication) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ADMIN can access any order
        if (authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            String email = authentication.getName();
            if (!order.getUser().getEmail().equals(email)) {
                throw new AccessDeniedException("You are not allowed to view this order");
            }
        }

        OrderDto dto = modelMapper.map(order, OrderDto.class);

        if (order.getPayment() != null) {
            dto.setPayment(modelMapper.map(order.getPayment(), PaymentDto.class));
        }

        return dto;
    }

    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public String cancelOrder(@PathVariable Long orderId, Authentication authentication) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔒 Ownership check
        String loggedInEmail = authentication.getName();
        if (!order.getUser().getEmail().equals(loggedInEmail)) {
            throw new AccessDeniedException("You cannot cancel this order");
        }

        // ❌ Business rule
        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel shipped or delivered order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return "Order cancelled successfully";
    }


    @PutMapping("/status/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus current = order.getStatus();

        // ❌ Prevent illegal transitions
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot change status of a completed order");
        }

        if (current == OrderStatus.SHIPPED && status == OrderStatus.CREATED) {
            throw new RuntimeException("Invalid status transition");
        }

        order.setStatus(status);
        orderRepository.save(order);

        return "Order status updated successfully";
    }


    @GetMapping("/allorders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDto> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(order -> {
                    OrderDto dto = modelMapper.map(order, OrderDto.class);

                    // Order items
                    dto.setItems(
                            order.getOrderItems().stream()
                                    .map(item -> {
                                        OrderItemDto itemDto = new OrderItemDto();
                                        itemDto.setId(item.getId());
                                        itemDto.setProductId(Long.valueOf(item.getProduct().getId()));
                                        itemDto.setProductName(item.getProduct().getName());
                                        itemDto.setPrice(item.getPrice());
                                        itemDto.setQuantity(item.getQuantity());
                                        return itemDto;
                                    })
                                    .collect(Collectors.toList())
                    );

                    // Payment
                    if (order.getPayment() != null) {
                        dto.setPayment(modelMapper.map(order.getPayment(), PaymentDto.class));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


}

