package com.example.sistem_pencatatan_konsumsi_gula_harian.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.DailyConsumptionDetail;
import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.SugarConsumptionForm;
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

        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            date = today;
        }

        if (authentication != null) {
            User user = authService.getUserByUsername(authentication.getName());
            if (user != null) {
                DailyConsumptionDetail detail = sugarConsumptionService.getDetailConsumption(user, date);
                model.addAttribute("name", user.getName());
                model.addAttribute("consumptions", detail.getConsumptions());
                model.addAttribute("dailyTotal", detail.getDailyTotal());
                model.addAttribute("dailyStatus", detail.getDailyStatus());
            } else {
                model.addAttribute("name", "");
            }
        } else {
            model.addAttribute("name", "");
        }
        model.addAttribute("selectedDate", date);
        model.addAttribute("maxDate", LocalDate.now().toString());
        model.addAttribute("maxDateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        model.addAttribute("sugarForm", new SugarConsumptionForm());
        return "dashboard";
    }
}
