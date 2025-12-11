package com.example.userservice.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private String avatarBase64;
}
