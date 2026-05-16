package com.example.sistem_pencatatan_konsumsi_gula_harian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.example.sistem_pencatatan_konsumsi_gula_harian.controllers.AuthController;
import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.RegisterForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.AuthService;

public class AuthControllerUnitTests {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authService);
    }

    @Test
    void loginReturnsLoginView() {
        String view = authController.login();
        assertEquals("login", view);
    }

    @Test
    void getRegisterAddsRegisterFormIfAbsent() {
        Model model = new ConcurrentModel();
        String view = authController.register(model);
        assertEquals("register", view);
        assertTrue(model.containsAttribute("registerForm"));
    }

    @Test
    void postRegisterSuccessRedirects() {
        RegisterForm form = new RegisterForm("Name", "user1", "pass123", "pass123");
        when(authService.register(form)).thenReturn(new User());

        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(form, "registerForm");
        RedirectAttributes redirect = new RedirectAttributesModelMap();

        String result = authController.register(form, binding, redirect);

        assertEquals("redirect:/login", result);
        verify(authService).register(form);
    }

    @Test
    void postRegisterPasswordMismatchReturnsForm() {
        RegisterForm form = new RegisterForm("Name", "user1", "pass123", "different");
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(form, "registerForm");
        RedirectAttributes redirect = new RedirectAttributesModelMap();

        String result = authController.register(form, binding, redirect);

        assertEquals("register", result);
    }

    @Test
    void postRegisterUsernameTakenReturnsForm() {
        RegisterForm form = new RegisterForm("Name", "takenUser", "pass123", "pass123");
        doThrow(new IllegalArgumentException("Username sudah digunakan")).when(authService).register(form);

        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(form, "registerForm");
        RedirectAttributes redirect = new RedirectAttributesModelMap();

        String result = authController.register(form, binding, redirect);

        assertEquals("register", result);
    }
}
