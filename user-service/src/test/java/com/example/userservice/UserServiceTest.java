package com.example.userservice;

import com.example.userservice.dto.*;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        userService = new UserService(userRepository, passwordEncoder, jwtUtil);
    }

    // ---------- register ----------

    @Test
    void register_createsNewUserAndReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@test.com");
        request.setPhoneNumber("+123456789");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(request.getEmail())).thenReturn("jwt_token_123");

        AuthResponse response = userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("John Doe", saved.getName());
        assertEquals("john@test.com", saved.getEmail());
        assertEquals("+123456789", saved.getPhoneNumber());
        assertEquals("encoded_password", saved.getPassword());

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertEquals(1L, response.getUser().getId());
        assertEquals("john@test.com", response.getUser().getEmail());
        assertEquals("jwt_token_123", response.getToken());
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        request.setPassword("pass");

        User existing = new User();
        existing.setEmail("existing@test.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existing));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(request));

        assertTrue(exception.getMessage().contains("уже есть"));
        verify(userRepository, never()).save(any(User.class));
    }

    // ---------- login ----------

    @Test
    void login_returnsAuthResponseWhenCredentialsValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("correct_password");

        User user = new User();
        user.setId(2L);
        user.setEmail("user@test.com");
        user.setName("Test User");
        user.setPassword("encoded_password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("login_token_456");

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals(2L, response.getUser().getId());
        assertEquals("user@test.com", response.getUser().getEmail());
        assertEquals("login_token_456", response.getToken());
    }

    @Test
    void login_throwsWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@test.com");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(request));

        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    void login_throwsWhenPasswordIncorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong_password");

        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("encoded_password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(request));

        assertTrue(exception.getMessage().contains("Неверный пароль"));
    }

    // ---------- getUser ----------

    @Test
    void getUser_returnsDtoWhenFound() {
        User user = new User();
        user.setId(3L);
        user.setName("Alice");
        user.setEmail("alice@test.com");
        user.setPhoneNumber("+987654321");
        user.setRole("ROLE_USER");

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        UserDto dto = userService.getUser(3L);

        assertEquals(3L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@test.com", dto.getEmail());
        assertEquals("+987654321", dto.getPhoneNumber());
        assertEquals("ROLE_USER", dto.getRole());
    }

    @Test
    void getUser_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUser(99L));

        assertTrue(exception.getMessage().contains("Нет такого пользователя"));
    }

    @Test
    void getUser_includesAvatarBase64WhenPresent() {
        User user = new User();
        user.setId(4L);
        user.setName("Bob");
        user.setEmail("bob@test.com");
        user.setAvatarBytes(new byte[]{1, 2, 3, 4, 5});

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        UserDto dto = userService.getUser(4L);

        assertNotNull(dto.getAvatarBase64());
        assertTrue(dto.getAvatarBase64().length() > 0);
    }

    // ---------- getAllUsers ----------

    @Test
    void getAllUsers_returnsDtos() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("User1");
        u1.setEmail("user1@test.com");
        u1.setRole("ROLE_USER");

        User u2 = new User();
        u2.setId(2L);
        u2.setName("User2");
        u2.setEmail("user2@test.com");
        u2.setRole("ROLE_ADMIN");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserDto> dtos = userService.getAllUsers();

        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("User1", dtos.get(0).getName());
        assertEquals(2L, dtos.get(1).getId());
        assertEquals("User2", dtos.get(1).getName());
    }

    @Test
    void getAllUsers_returnsEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> dtos = userService.getAllUsers();

        assertTrue(dtos.isEmpty());
    }
}
