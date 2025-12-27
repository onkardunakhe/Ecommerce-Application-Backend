package com.Crud.Crud.Service;

import com.Crud.Crud.Dtos.AddToCartRequest;
import com.Crud.Crud.Dtos.CartResponse;
import com.Crud.Crud.Entity.Cart;
import com.Crud.Crud.Entity.CartItem;
import com.Crud.Crud.Entity.Product;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.CartItemRepository;
import com.Crud.Crud.Repository.CartRepository;
import com.Crud.Crud.Repository.Prodrepo;
import com.Crud.Crud.Repository.Userrepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final Userrepo userRepository;
    private final Prodrepo productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // 1️⃣ ADD TO CART
    @Transactional
    public void addToCart(AddToCartRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Get or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Get or create cart item
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElseGet(() -> {
                    CartItem item = new CartItem();
                    item.setCart(cart);
                    item.setProduct(product);
                    item.setQuantity(0);
                    item.setUser(user);
                    return item;
                });

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());

        cartItemRepository.save(cartItem);
    }

    // 2️⃣ GET USER CART
    public List<CartResponse> getUserCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cartItemRepository.findByCart(cart)
                .stream()
                .map(item -> new CartResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()

                ))
                .toList();
    }

    // 3️⃣ CLEAR CART
    @Transactional
    public void clearCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteByCart(cart);
    }
}
