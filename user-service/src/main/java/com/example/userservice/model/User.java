package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String phoneNumber;
    private String password;
    private boolean active = true;

    @Lob
    private byte[] avatarBytes;

    private String avatarFileName;
    private String role = "ROLE_USER";
    private LocalDateTime createdDate = LocalDateTime.now();
}
