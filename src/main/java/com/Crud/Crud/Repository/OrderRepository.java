package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.Order;
import com.Crud.Crud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
