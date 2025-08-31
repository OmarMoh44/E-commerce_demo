package org.ecommerce.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.CartItemMainView;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping
    public void createCart(@AuthenticationPrincipal User user) {
        cartService.createCart(user);
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/cartItems")
    public List<CartItemMainView> getCartItems(@AuthenticationPrincipal User user) {
        return cartService.getCartItems(user);
    }

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping
    public void deleteCart(@AuthenticationPrincipal User user) {
        cartService.deleteCart(user);
    }
}

