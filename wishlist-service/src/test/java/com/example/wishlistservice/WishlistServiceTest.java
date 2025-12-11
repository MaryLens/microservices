package com.example.wishlistservice;

import com.example.wishlistservice.dto.WishlistDto;
import com.example.wishlistservice.model.Wishlist;
import com.example.wishlistservice.repository.WishlistRepository;
import com.example.wishlistservice.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistServiceTest {

    private WishlistRepository wishlistRepository;
    private WishlistService wishlistService;

    @BeforeEach
    void setUp() {
        wishlistRepository = mock(WishlistRepository.class);
        wishlistService = new WishlistService(wishlistRepository);
    }

    // ---------- getWishlist ----------

    @Test
    void getWishlist_returnsExistingWishlist() {
        Long userId = 1L;
        Wishlist existing = new Wishlist();
        existing.setId(10L);
        existing.setUserId(userId);
        existing.setProducts(new HashSet<>(List.of(100L, 200L)));

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        WishlistDto dto = wishlistService.getWishlist(userId);

        verify(wishlistRepository, never()).save(any(Wishlist.class));
        assertEquals(10L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(2, dto.getProducts().size());
        assertTrue(dto.getProducts().contains(100L));
        assertTrue(dto.getProducts().contains(200L));
    }

    @Test
    void getWishlist_createsNewWishlistWhenNotExists() {
        Long userId = 2L;

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(inv -> {
            Wishlist wl = inv.getArgument(0);
            wl.setId(20L);
            if (wl.getProducts() == null) wl.setProducts(new HashSet<>());
            return wl;
        });

        WishlistDto dto = wishlistService.getWishlist(userId);

        ArgumentCaptor<Wishlist> captor = ArgumentCaptor.forClass(Wishlist.class);
        verify(wishlistRepository).save(captor.capture());
        Wishlist saved = captor.getValue();

        assertEquals(userId, saved.getUserId());
        assertEquals(20L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertNotNull(dto.getProducts());
    }

    // ---------- addProduct ----------

    @Test
    void addProduct_addsProductToExistingWishlist() {
        Long userId = 1L;
        Long productId = 300L;

        Wishlist existing = new Wishlist();
        existing.setId(5L);
        existing.setUserId(userId);
        existing.setProducts(new HashSet<>(List.of(100L, 200L)));

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(inv -> inv.getArgument(0));

        WishlistDto dto = wishlistService.addProduct(userId, productId);

        verify(wishlistRepository).save(existing);
        assertEquals(3, existing.getProducts().size());
        assertTrue(existing.getProducts().contains(productId));

        assertEquals(5L, dto.getId());
        assertEquals(3, dto.getProducts().size());
        assertTrue(dto.getProducts().contains(300L));
    }

    @Test
    void addProduct_createsWishlistIfNotExists() {
        Long userId = 3L;
        Long productId = 400L;

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(inv -> {
            Wishlist wl = inv.getArgument(0);
            if (wl.getId() == null) wl.setId(30L);
            if (wl.getProducts() == null) wl.setProducts(new HashSet<>());
            return wl;
        });

        WishlistDto dto = wishlistService.addProduct(userId, productId);

        ArgumentCaptor<Wishlist> captor = ArgumentCaptor.forClass(Wishlist.class);
        verify(wishlistRepository, atLeast(1)).save(captor.capture());

        // Проверяем последнее сохранение
        List<Wishlist> saved = captor.getAllValues();
        Wishlist last = saved.get(saved.size() - 1);

        assertEquals(userId, last.getUserId());
        assertTrue(last.getProducts().contains(productId));

        assertEquals(30L, dto.getId());
        assertTrue(dto.getProducts().contains(productId));
    }

    // ---------- removeProduct ----------

    @Test
    void removeProduct_removesProductFromWishlist() {
        Long userId = 1L;
        Long productId = 200L;

        Wishlist wishlist = new Wishlist();
        wishlist.setId(7L);
        wishlist.setUserId(userId);
        wishlist.setProducts(new HashSet<>(List.of(100L, 200L, 300L)));

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(inv -> inv.getArgument(0));

        WishlistDto dto = wishlistService.removeProduct(userId, productId);

        verify(wishlistRepository).save(wishlist);
        assertEquals(2, wishlist.getProducts().size());
        assertFalse(wishlist.getProducts().contains(productId));
        assertTrue(wishlist.getProducts().contains(100L));
        assertTrue(wishlist.getProducts().contains(300L));

        assertEquals(7L, dto.getId());
        assertEquals(2, dto.getProducts().size());
        assertFalse(dto.getProducts().contains(productId));
    }

    @Test
    void removeProduct_throwsWhenWishlistNotFound() {
        Long userId = 99L;
        Long productId = 100L;

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> wishlistService.removeProduct(userId, productId));

        assertEquals("Not found", exception.getMessage());
        verify(wishlistRepository, never()).save(any(Wishlist.class));
    }

    @Test
    void removeProduct_handlesNonExistentProduct() {
        Long userId = 1L;
        Long productId = 999L;

        Wishlist wishlist = new Wishlist();
        wishlist.setId(8L);
        wishlist.setUserId(userId);
        wishlist.setProducts(new HashSet<>(List.of(100L, 200L)));

        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(inv -> inv.getArgument(0));

        WishlistDto dto = wishlistService.removeProduct(userId, productId);

        // Товара не было, список не изменится
        assertEquals(2, wishlist.getProducts().size());
        assertEquals(2, dto.getProducts().size());
    }

    // ---------- toDto mapping ----------

    @Test
    void toDto_mapsWishlistCorrectly() {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(15L);
        wishlist.setUserId(5L);
        wishlist.setProducts(new HashSet<>(List.of(10L, 20L, 30L)));

        when(wishlistRepository.findByUserId(5L)).thenReturn(Optional.of(wishlist));

        WishlistDto dto = wishlistService.getWishlist(5L);

        assertEquals(15L, dto.getId());
        assertEquals(5L, dto.getUserId());
        assertEquals(3, dto.getProducts().size());
        assertTrue(dto.getProducts().containsAll(List.of(10L, 20L, 30L)));
    }
}

