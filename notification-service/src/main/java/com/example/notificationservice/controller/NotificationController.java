package com.example.notificationservice.controller;

import com.example.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final EmailService emailService;

    @PostMapping("/order")
    public void sendOrderEmail(@RequestParam String email, @RequestParam String subject, @RequestParam String text) {
        emailService.sendOrderEmail(email, subject, text);
    }
}
