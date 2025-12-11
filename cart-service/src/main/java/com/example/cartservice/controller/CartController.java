package com.example.cartservice.controller;

import com.example.cartservice.dto.CartDto;
import com.example.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartDto> addItem(@PathVariable Long userId,
                                           @RequestParam Long productId,
                                           @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addItem(userId, productId, quantity));
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDto> updateItem(@PathVariable Long userId,
                                              @PathVariable Long productId,
                                              @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItem(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDto> removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
