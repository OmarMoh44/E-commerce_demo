package org.ecommerce.backend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.AddCartItemRequest;
import org.ecommerce.backend.dto.CartItemMainView;
import org.ecommerce.backend.dto.ProductMainView;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.CartItem;
import org.ecommerce.backend.model.Product;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.CartItemRepository;
import org.ecommerce.backend.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final ModelMapper modelMapper;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    @CacheEvict(value = "cart-items", key = "#user.id")
    public CartItemMainView addCartItem(User user, AddCartItemRequest request) {
        if (user.getCart() == null)
            throw new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getMessage());

        CartItem existedCartItem = cartItemRepository.findAllByCartId(user.getCart().getId()).stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existedCartItem != null) {
            throw new EntityExistsException(ErrorMessage.CART_ITEM_ALREADY_EXISTS.getMessage());
        }
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.PRODUCT_NOT_FOUND.getMessage())
        );
        CartItem cartItem = new CartItem();
        cartItem.setCart(user.getCart());
        cartItem.setProduct(product);
        cartItem.setQuantity(request.getQuantity());
        CartItemMainView view = modelMapper.map(cartItemRepository.save(cartItem), CartItemMainView.class);
        view.setProductMainView(modelMapper.map(product, ProductMainView.class));
        return view;
    }

    @Cacheable(value = "cart-item", key = "#id")
    public CartItemMainView getCartItem(Long id, User user) {
        if (user.getCart() == null)
            throw new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getMessage());
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.CART_ITEM_NOT_EXISTS.getMessage())
        );

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException(ErrorMessage.CART_ITEM_NOT_EXISTS.getMessage());
        }

        CartItemMainView view = modelMapper.map(cartItem, CartItemMainView.class);
        view.setProductMainView(modelMapper.map(cartItem.getProduct(), ProductMainView.class));
        return view;
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cart-items", key = "#user.id"),
                    @CacheEvict(value = "cart-item", key = "#id")
            }
    )
    public void deleteCartItem(Long id, User user) {
        if (user.getCart() == null)
            throw new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getMessage());
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.CART_ITEM_NOT_EXISTS.getMessage())
        );

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException(ErrorMessage.CART_ITEM_NOT_EXISTS.getMessage());
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    @Caching(
            evict = @CacheEvict(value = "cart-items", key = "#user.id"),
            put = @CachePut(value = "cart-item", key = "#id")
    )
    public CartItemMainView updateCartItem(User user, Integer quantity, Long id) {
        if (user.getCart() == null)
            throw new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getMessage());
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.CART_ITEM_NOT_EXISTS.getMessage())
        );

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException(ErrorMessage.CART_ITEM_NOT_EXISTS.getMessage());
        }

        cartItem.setQuantity(quantity);
        CartItemMainView view = modelMapper.map(cartItemRepository.save(cartItem), CartItemMainView.class);
        view.setProductMainView(modelMapper.map(cartItem.getProduct(), ProductMainView.class));
        return view;
    }
}
