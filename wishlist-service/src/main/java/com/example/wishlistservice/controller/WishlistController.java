package com.example.wishlistservice.controller;

import com.example.wishlistservice.dto.WishlistDto;
import com.example.wishlistservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    public ResponseEntity<WishlistDto> getWishlist(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @PostMapping("/{userId}/products")
    public ResponseEntity<WishlistDto> addProduct(@PathVariable Long userId, @RequestParam Long productId) {
        return ResponseEntity.ok(wishlistService.addProduct(userId, productId));
    }

    @DeleteMapping("/{userId}/products/{productId}")
    public ResponseEntity<WishlistDto> removeProduct(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.removeProduct(userId, productId));
    }
}
