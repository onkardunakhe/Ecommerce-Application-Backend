package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.Cart;
import com.Crud.Crud.Entity.CartItem;
import com.Crud.Crud.Entity.Product;
import com.Crud.Crud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    List<CartItem> findByCart(Cart cart);

    void deleteByCart(Cart cart);

    List<CartItem> findByUser(User user);
}
