package com.example.orderservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private int total;
    private String status;
    private String createdDate;
    private List<OrderItemDto> items;
}