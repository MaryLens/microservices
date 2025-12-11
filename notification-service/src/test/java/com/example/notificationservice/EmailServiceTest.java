package com.example.notificationservice;

import com.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() throws Exception {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService();

        // Внедряем мок через reflection, т.к. используется @Autowired
        Field field = EmailService.class.getDeclaredField("mailSender");
        field.setAccessible(true);
        field.set(emailService, mailSender);
    }

    @Test
    void sendOrderEmail_buildsAndSendsMailCorrectly() {
        // given
        String to = "user@test.com";
        String subject = "Order created";
        String text = "Your order #123 has been created";

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // when
        emailService.sendOrderEmail(to, subject, text);

        // then
        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertEquals("mary.not.muggle@gmail.com", sent.getFrom());
        assertNotNull(sent.getTo());
        assertEquals(1, sent.getTo().length);
        assertEquals(to, sent.getTo()[0]);
        assertEquals(subject, sent.getSubject());
        assertEquals(text, sent.getText());
    }

    @Test
    void sendOrderEmail_callsMailSenderSend() {
        // given
        String to = "customer@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        // when
        emailService.sendOrderEmail(to, subject, text);

        // then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

