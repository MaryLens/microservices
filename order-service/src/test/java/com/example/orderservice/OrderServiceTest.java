package com.example.orderservice;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        // Создаём реальный сервис
        OrderService realService = new OrderService(orderRepository);
        // И делаем spy, чтобы подменить sendOrderNotification
        orderService = spy(realService);
    }

    // ---------- getOrdersByUser ----------

    @Test
    void getOrdersByUser_returnsDtos() {
        Long userId = 1L;
        Order order1 = new Order();
        order1.setId(10L);
        order1.setUserId(userId);
        order1.setTotal(500);
        order1.setStatus("NEW");
        order1.setCreatedDate(LocalDateTime.now());
        order1.setItems(new ArrayList<>());

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order1));

        List<OrderDto> result = orderService.getOrdersByUser(userId);

        verify(orderRepository).findByUserId(userId);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(500, result.get(0).getTotal());
        assertEquals("NEW", result.get(0).getStatus());
    }

    @Test
    void getOrdersByUser_returnsEmptyListWhenNoOrders() {
        when(orderRepository.findByUserId(2L)).thenReturn(List.of());

        List<OrderDto> result = orderService.getOrdersByUser(2L);

        assertTrue(result.isEmpty());
    }

    // ---------- getOrder ----------

    @Test
    void getOrder_returnsDtoWhenFound() {
        Order order = new Order();
        order.setId(5L);
        order.setUserId(2L);
        order.setTotal(200);
        order.setStatus("PAID");
        order.setCreatedDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        OrderDto dto = orderService.getOrder(5L);

        assertEquals(5L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals(200, dto.getTotal());
        assertEquals("PAID", dto.getStatus());
    }

    @Test
    void getOrder_throwsWhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrder(99L));
        assertEquals("Not found", exception.getMessage());
    }

    // ---------- createOrder ----------

    @Test
    void createOrder_savesOrderAndSendsNotification() {
        Long userId = 1L;
        String email = "user@test.com";
        int total = 300;

        OrderItem item1 = new OrderItem();
        item1.setProductId(10L);
        item1.setPrice(100);
        item1.setQuantity(3);

        List<OrderItem> items = new ArrayList<>();
        items.add(item1);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(77L);
            o.setCreatedDate(LocalDateTime.now());
            return o;
        });

        // Подменяем HTTP-вызов, чтобы не делать реальный запрос
        doNothing().when(orderService).sendOrderNotification(anyString(), anyLong());

        OrderDto dto = orderService.createOrder(userId, email, items, total);

        // Проверяем сохранение
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();

        assertEquals(userId, saved.getUserId());
        assertEquals(total, saved.getTotal());
        assertEquals("NEW", saved.getStatus());
        assertEquals(1, saved.getItems().size());
        assertEquals(saved, items.get(0).getOrder());

        // Проверяем DTO
        assertEquals(77L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(total, dto.getTotal());
        assertEquals("NEW", dto.getStatus());
        assertEquals(1, dto.getItems().size());

        // Проверяем отправку уведомления
        verify(orderService).sendOrderNotification(email, 77L);
    }

    @Test
    void createOrder_continuesWhenNotificationFails() {
        Long userId = 1L;
        String email = "user@test.com";
        int total = 100;

        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setPrice(100);
        item.setQuantity(1);
        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(5L);
            o.setCreatedDate(LocalDateTime.now());
            return o;
        });

        // Симулируем ошибку при отправке
        doThrow(new RuntimeException("Network error"))
                .when(orderService).sendOrderNotification(email, 5L);

        // Заказ всё равно должен создаться
        OrderDto dto = orderService.createOrder(userId, email, items, total);

        assertEquals(5L, dto.getId());
        assertEquals(userId, dto.getUserId());
        verify(orderService).sendOrderNotification(email, 5L);
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatus_changesStatusAndSaves() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus("NEW");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateStatus(1L, "PAID");

        assertEquals("PAID", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateStatus_throwsWhenNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.updateStatus(1L, "PAID"));
        assertEquals("Not found", exception.getMessage());
    }

    // ---------- getAllOrders ----------

    @Test
    void getAllOrders_returnsDtos() {
        Order o1 = new Order();
        o1.setId(1L);
        o1.setUserId(1L);
        o1.setTotal(100);
        o1.setStatus("NEW");
        o1.setCreatedDate(LocalDateTime.now());
        o1.setItems(new ArrayList<>());

        Order o2 = new Order();
        o2.setId(2L);
        o2.setUserId(2L);
        o2.setTotal(200);
        o2.setStatus("PAID");
        o2.setCreatedDate(LocalDateTime.now());
        o2.setItems(new ArrayList<>());

        when(orderRepository.findAll()).thenReturn(List.of(o1, o2));

        List<OrderDto> dtos = orderService.getAllOrders();

        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(2L, dtos.get(1).getId());
    }

    // ---------- deleteOrder ----------

    @Test
    void deleteOrder_callsRepository() {
        orderService.deleteOrder(10L);

        verify(orderRepository).deleteById(10L);
    }

}

