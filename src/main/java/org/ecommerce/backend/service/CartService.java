package org.ecommerce.backend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.CartItemMainView;
import org.ecommerce.backend.dto.ProductMainView;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.Cart;
import org.ecommerce.backend.model.CartItem;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.CartItemRepository;
import org.ecommerce.backend.repository.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public void createCart(User user) {
        if (user.getCart() != null) {
            throw new EntityExistsException(ErrorMessage.CART_ALREADY_EXISTS.getMessage());
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        // Update the user's cart reference
        user.setCart(cart);
    }

    @Cacheable(value = "cart-items", key = "#user.id")
    public List<CartItemMainView> getCartItems(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            return List.of();
        }

        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());
        return cartItems.stream()
                .map(item -> {
                    CartItemMainView view = modelMapper.map(item, CartItemMainView.class);
                    view.setProductMainView(modelMapper.map(item.getProduct(), ProductMainView.class));
                    return view;
                })
                .toList();
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cart-items", key = "#user.id"),
                    @CacheEvict(value = "cart-item", allEntries = true)
            }
    )
    public void deleteCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getMessage()));

        // Explicitly delete cart items first to handle foreign key constraints
        cartItemRepository.deleteAllByCartId(cart.getId());
        
        // Break the user-cart relationship
        user.setCart(null);
        
        // Delete the cart
        cartRepository.deleteById(cart.getId());
    }
}
