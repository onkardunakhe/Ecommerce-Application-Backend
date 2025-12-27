package com.Crud.Crud.Controllers;

import com.Crud.Crud.Dtos.AddToCartRequest;
import com.Crud.Crud.Dtos.CartResponse;
import com.Crud.Crud.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication) {

        cartService.addToCart(request, authentication.getName());
        return ResponseEntity.ok("Product added to cart");
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<CartResponse> getCart(Authentication authentication) {
        return cartService.getUserCart(authentication.getName());
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.ok("Cart cleared");
    }

}
