package com.example.sistem_pencatatan_konsumsi_gula_harian.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.AuthService;

@Controller
@RequestMapping
public class DashboardController {

    private final AuthService authService;

    public DashboardController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            User user = authService.getUserByUsername(authentication.getName());
            model.addAttribute("name", user != null ? user.getName() : "");
        } else {
            model.addAttribute("name", "");
        }
        return "dashboard";
    }
}
