package com.example.sistem_pencatatan_konsumsi_gula_harian.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.RegisterForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.AuthService;

import jakarta.validation.Valid;

@Controller
@RequestMapping
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!registerForm.isPasswordMatch()) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Password dan konfirmasi password harus sama");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            authService.register(registerForm);
            redirectAttributes.addFlashAttribute("registerSuccess", "Registrasi berhasil. Silakan login.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("username", "username.taken", ex.getMessage());
            return "register";
        }
    }

}
