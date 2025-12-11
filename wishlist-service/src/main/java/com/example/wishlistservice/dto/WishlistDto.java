package com.example.wishlistservice.dto;

import lombok.Data;
import java.util.Set;

@Data
public class WishlistDto {
    private Long id;
    private Long userId;
    private Set<Long> products;
}
