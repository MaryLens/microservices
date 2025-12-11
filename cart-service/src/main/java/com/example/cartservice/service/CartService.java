package com.example.cartservice.service;

import com.example.cartservice.dto.CartDto;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.model.Cart;
import com.example.cartservice.model.CartItem;
import com.example.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    cartRepository.save(newCart);
                    return newCart;
                });
        return toDto(cart);
    }

    @Transactional
    public CartDto addItem(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProductId(productId);
            item.setQuantity(quantity);
            cart.getItems().add(item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        cartRepository.save(cart);
        return toDto(cart);
    }

    @Transactional
    public CartDto updateItem(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setQuantity(quantity);
        cartRepository.save(cart);
        return toDto(cart);
    }

    @Transactional
    public CartDto removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().removeIf(ci -> ci.getProductId().equals(productId));
        cartRepository.save(cart);
        return toDto(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartDto toDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setItems(cart.getItems().stream()
                .map(ci -> {
                    CartItemDto itemDto = new CartItemDto();
                    itemDto.setProductId(ci.getProductId());
                    itemDto.setQuantity(ci.getQuantity());
                    return itemDto;
                }).collect(Collectors.toList()));
        return dto;
    }
}
