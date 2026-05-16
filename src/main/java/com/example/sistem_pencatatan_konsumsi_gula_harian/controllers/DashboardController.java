package com.example.sistem_pencatatan_konsumsi_gula_harian.controllers;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.AuthService;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.SugarConsumptionService;

@Controller
@RequestMapping
public class DashboardController {

    private final AuthService authService;
    private final SugarConsumptionService sugarConsumptionService;

    public DashboardController(AuthService authService, SugarConsumptionService sugarConsumptionService) {
        this.authService = authService;
        this.sugarConsumptionService = sugarConsumptionService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication authentication, Model model, @RequestParam(name = "date", required = false) String dateParam) {
        LocalDate date;
        if (dateParam == null || dateParam.isBlank()) {
            date = LocalDate.now();
        } else {
            date = LocalDate.parse(dateParam);
        }

        if (authentication != null) {
            User user = authService.getUserByUsername(authentication.getName());
            model.addAttribute("name", user != null ? user.getName() : "");
            if (user != null) {
                model.addAttribute("consumptions", sugarConsumptionService.getConsumptionsForDate(user, date));
            }
        } else {
            model.addAttribute("name", "");
        }
        model.addAttribute("selectedDate", date);
        model.addAttribute("sugarForm", new com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.SugarConsumptionForm());
        return "dashboard";
    }
}
