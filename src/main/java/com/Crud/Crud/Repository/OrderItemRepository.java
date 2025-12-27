package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.Order;
import com.Crud.Crud.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
