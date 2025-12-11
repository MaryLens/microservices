package com.example.wishlistservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wishlists")
@Data
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "wishlist_products", joinColumns = @JoinColumn(name = "wishlist_id"))
    @Column(name = "product_id")
    private Set<Long> products = new HashSet<>();
}
