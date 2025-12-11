package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderDto> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public OrderDto getOrder(Long id) {
        return orderRepository.findById(id).map(this::toDto).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public OrderDto createOrder(Long userId, String userEmail, List<OrderItem> items, int total) {
        Order order = new Order();
        order.setUserId(userId);
        order.setTotal(total);
        order.setStatus("NEW");
        order.setItems(items);
        items.forEach(i -> i.setOrder(order));
        orderRepository.save(order);
        try {
            sendOrderNotification(userEmail, order.getId());
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
        return toDto(order);
    }

    public void updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setCreatedDate(String.valueOf(order.getCreatedDate()));
        dto.setItems(order.getItems().stream().map(oi -> {
            OrderItemDto oidto = new OrderItemDto();
            oidto.setProductId(oi.getProductId());
            oidto.setQuantity(oi.getQuantity());
            oidto.setPrice(oi.getPrice());
            return oidto;
        }).collect(Collectors.toList()));
        return dto;
    }

    public void sendOrderNotification(String userEmail, Long orderId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://notification-service:8086/api/notifications/order";
        //String url = "http://localhost:8086/api/notifications/order";
        restTemplate.postForObject(url +
                "?email=" + userEmail +
                "&subject=Your order successfully created" +
                "&text=Your order #" + orderId + " successfully created!", null, String.class);
    }
}
