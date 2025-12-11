package com.example.cartservice;


import com.example.cartservice.dto.CartDto;
import com.example.cartservice.model.Cart;
import com.example.cartservice.model.CartItem;
import com.example.cartservice.repository.CartRepository;
import com.example.cartservice.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartRepository cartRepository;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        cartService = new CartService(cartRepository);
    }

    // ---------- getCart ----------

    @Test
    void getCart_returnsExistingCart() {
        Long userId = 1L;
        Cart existing = new Cart();
        existing.setId(10L);
        existing.setUserId(userId);
        existing.setItems(new ArrayList<>());

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        CartDto dto = cartService.getCart(userId);

        verify(cartRepository, never()).save(any(Cart.class));
        assertEquals(10L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void getCart_createsNewCartWhenNotExists() {
        Long userId = 2L;
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            c.setId(20L);
            if (c.getItems() == null) c.setItems(new ArrayList<>());
            return c;
        });

        CartDto dto = cartService.getCart(userId);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart saved = captor.getValue();

        assertEquals(userId, saved.getUserId());
        assertEquals(20L, dto.getId());
        assertEquals(userId, dto.getUserId());
    }


    @Test
    void addItem_addsNewItemWhenNotPresent() {
        Long userId = 1L;
        Long productId = 100L;

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDto dto = cartService.addItem(userId, productId, 3);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        CartItem item = cart.getItems().get(0);
        assertEquals(productId, item.getProductId());
        assertEquals(3, item.getQuantity());

        assertEquals(1, dto.getItems().size());
        assertEquals(productId, dto.getItems().get(0).getProductId());
        assertEquals(3, dto.getItems().get(0).getQuantity());
    }

    @Test
    void addItem_increasesQuantityWhenItemExists() {
        Long userId = 1L;
        Long productId = 100L;

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        CartItem existing = new CartItem();
        existing.setProductId(productId);
        existing.setQuantity(2);
        existing.setCart(cart);

        List<CartItem> items = new ArrayList<>();
        items.add(existing);
        cart.setItems(items);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDto dto = cartService.addItem(userId, productId, 5);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(7, dto.getItems().get(0).getQuantity());
    }

    @Test
    void addItem_throwsWhenCartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> cartService.addItem(1L, 10L, 1));
    }


    @Test
    void updateItem_changesQuantityOfExistingItem() {
        Long userId = 1L;
        Long productId = 100L;

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        CartItem existing = new CartItem();
        existing.setProductId(productId);
        existing.setQuantity(2);
        existing.setCart(cart);
        cart.setItems(new ArrayList<>(List.of(existing)));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDto dto = cartService.updateItem(userId, productId, 10);

        verify(cartRepository).save(cart);
        assertEquals(10, cart.getItems().get(0).getQuantity());
        assertEquals(10, dto.getItems().get(0).getQuantity());
    }

    @Test
    void updateItem_throwsWhenCartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> cartService.updateItem(1L, 100L, 5));
    }

    @Test
    void updateItem_throwsWhenItemNotFound() {
        Long userId = 1L;
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(RuntimeException.class,
                () -> cartService.updateItem(userId, 999L, 5));
    }


    @Test
    void removeItem_removesItemFromCart() {
        Long userId = 1L;
        Long productId = 100L;

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        CartItem item1 = new CartItem();
        item1.setProductId(productId);
        item1.setQuantity(3);
        item1.setCart(cart);

        CartItem item2 = new CartItem();
        item2.setProductId(200L);
        item2.setQuantity(1);
        item2.setCart(cart);

        cart.setItems(new ArrayList<>(List.of(item1, item2)));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDto dto = cartService.removeItem(userId, productId);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(1, dto.getItems().size());
        assertEquals(200L, dto.getItems().get(0).getProductId());
    }

    @Test
    void removeItem_throwsWhenCartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> cartService.removeItem(1L, 100L));
    }


    @Test
    void clearCart_removesAllItems() {
        Long userId = 1L;

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        CartItem item1 = new CartItem();
        item1.setProductId(100L);
        item1.setQuantity(3);
        item1.setCart(cart);

        cart.setItems(new ArrayList<>(List.of(item1)));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.clearCart(userId);

        verify(cartRepository).save(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void clearCart_throwsWhenCartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> cartService.clearCart(1L));
    }
}
