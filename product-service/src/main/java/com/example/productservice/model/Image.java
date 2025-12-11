package com.example.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "images")
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalFileName;

    @Lob
    private byte[] bytes;

    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;
}
