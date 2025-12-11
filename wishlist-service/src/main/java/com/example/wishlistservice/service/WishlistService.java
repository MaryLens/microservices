package com.example.wishlistservice.service;

import com.example.wishlistservice.dto.WishlistDto;
import com.example.wishlistservice.model.Wishlist;
import com.example.wishlistservice.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    public WishlistDto getWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist wl = new Wishlist();
                    wl.setUserId(userId);
                    wishlistRepository.save(wl);
                    return wl;
                });
        return toDto(wishlist);
    }

    public WishlistDto addProduct(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist wl = new Wishlist();
                    wl.setUserId(userId);
                    wishlistRepository.save(wl);
                    return wl;
                });
        wishlist.getProducts().add(productId);
        wishlistRepository.save(wishlist);
        return toDto(wishlist);
    }

    public WishlistDto removeProduct(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        wishlist.getProducts().remove(productId);
        wishlistRepository.save(wishlist);
        return toDto(wishlist);
    }

    private WishlistDto toDto(Wishlist wl) {
        WishlistDto dto = new WishlistDto();
        dto.setId(wl.getId());
        dto.setUserId(wl.getUserId());
        dto.setProducts(wl.getProducts());
        return dto;
    }
}
