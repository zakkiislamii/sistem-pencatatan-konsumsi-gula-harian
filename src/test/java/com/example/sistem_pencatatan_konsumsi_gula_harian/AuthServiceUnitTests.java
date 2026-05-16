package com.example.sistem_pencatatan_konsumsi_gula_harian;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.RegisterForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.repositories.UserRepository;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.AuthService;

public class AuthServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    void registerSuccessCreatesNewUser() {
        RegisterForm form = new RegisterForm("John Doe", "johndoe", "password123", "password123");
        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setName(form.getName());
        savedUser.setUsername(form.getUsername());
        savedUser.setPassword("encodedPassword");

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.register(form);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("John Doe", result.getName());
        assertEquals(1, result.getUserId());
        verify(userRepository).existsByUsername("johndoe");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerThrowsExceptionWhenUsernameAlreadyExists() {
        RegisterForm form = new RegisterForm("Jane Doe", "existinguser", "password123", "password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.register(form));

        assertEquals("Username sudah digunakan", exception.getMessage());
        verify(userRepository).existsByUsername("existinguser");
    }

    @Test
    void loadUserByUsernameSuccessReturnsUserDetails() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setName("Test User");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails result = authService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsernameThrowsExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, ()
                -> authService.loadUserByUsername("nonexistentuser"));

        assertEquals("User tidak ditemukan", exception.getMessage());
        verify(userRepository).findByUsername("nonexistentuser");
    }

    @Test
    void getUserByUsernameSuccessReturnsUser() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setName("Test User");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = authService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1, result.getUserId());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserByUsernameReturnsNullWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        User result = authService.getUserByUsername("nonexistentuser");
        assertNull(result);
        verify(userRepository).findByUsername("nonexistentuser");
    }
}
