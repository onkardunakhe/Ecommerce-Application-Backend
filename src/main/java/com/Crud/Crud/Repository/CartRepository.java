package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.Cart;
import com.Crud.Crud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {


    Optional<Cart> findByUser(User user);

    void deleteByUser(User user);


}
