package org.ecommerce.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.AddCartItemRequest;
import org.ecommerce.backend.dto.CartItemMainView;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.service.CartItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-item")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping
    public CartItemMainView addCartItem(@AuthenticationPrincipal User user,
                                        @RequestBody @Valid AddCartItemRequest request) {
        return cartItemService.addCartItem(user, request);
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/{id}")
    public CartItemMainView getCartItem(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return cartItemService.getCartItem(id, user);
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/{id}")
    public CartItemMainView updateCartItem(@PathVariable Long id,
                                           @RequestParam Integer quantity,
                                           @AuthenticationPrincipal User user) {
        return cartItemService.updateCartItem(user, quantity, id);
    }

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{id}")
    public void deleteCartItem(@PathVariable Long id, @AuthenticationPrincipal User user) {
        cartItemService.deleteCartItem(id, user);
    }
}
