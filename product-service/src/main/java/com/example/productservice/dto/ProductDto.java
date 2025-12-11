package com.example.productservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private int price;
    private boolean isOnSale;
    private Long categoryId;
    private String categoryName;
    private List<String> imagesBase64;
}
