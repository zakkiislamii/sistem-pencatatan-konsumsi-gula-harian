package com.example.sistem_pencatatan_konsumsi_gula_harian.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.SugarConsumptionForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.AuthService;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.SugarConsumptionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping
public class SugarConsumptionController {

    private final SugarConsumptionService consumptionService;
    private final AuthService authService;

    public SugarConsumptionController(SugarConsumptionService consumptionService, AuthService authService) {
        this.consumptionService = consumptionService;
        this.authService = authService;
    }

    @PostMapping("/consumption/add")
    public String addProcess(
            @Valid @ModelAttribute("sugarForm") SugarConsumptionForm form,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Periksa kembali inputan kamu");
            return "redirect:/dashboard";
        }

        User user = authService.getUserByUsername(authentication.getName());
        try {
            consumptionService.addConsumption(form, user);
            redirectAttributes.addFlashAttribute("success", "Data berhasil ditambahkan");
            String date = form.getConsumedAt().toLocalDate().toString();
            return "redirect:/dashboard?date=" + date;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/consumption/{id}/edit")
    public String editProcess(
            @PathVariable Integer id,
            @Valid @ModelAttribute("sugarForm") SugarConsumptionForm form,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Periksa kembali inputan kamu");
            return "redirect:/dashboard";
        }

        User user = authService.getUserByUsername(authentication.getName());
        try {
            consumptionService.updateConsumption(id, form, user);
            redirectAttributes.addFlashAttribute("success", "Data berhasil diperbarui");
            String date = form.getConsumedAt().toLocalDate().toString();
            return "redirect:/dashboard?date=" + date;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/consumption/{id}/fragment")
    public String fragmentForEdit(
            @PathVariable Integer id,
            Authentication authentication,
            Model model) {

        if (authentication == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        User user = authService.getUserByUsername(authentication.getName());
        Optional<SugarConsumption> opt = consumptionService.findByIdForUser(id, user);
        if (opt.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND);
        }

        SugarConsumption sc = opt.get();
        SugarConsumptionForm form = new SugarConsumptionForm(
                sc.getConsumptionId(),
                sc.getAmount(),
                sc.getDescription(),
                sc.getConsumedAt());
        model.addAttribute("sugarForm", form);
        model.addAttribute("maxDateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        return "fragments/consumption-form-fragment :: consumptionForm";
    }

    @PostMapping("/consumption/{id}/delete")
    public String deleteConsumption(
            @PathVariable Integer id,
            Authentication authentication,
            @RequestParam(name = "date", required = false) String dateParam,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = authService.getUserByUsername(authentication.getName());
        try {
            consumptionService.deleteConsumption(id, user);
            redirectAttributes.addFlashAttribute("success", "Data berhasil dihapus");
            String redirectDate = (dateParam == null || dateParam.isBlank())
                    ? LocalDateTime.now().toLocalDate().toString()
                    : dateParam;
            return "redirect:/dashboard?date=" + redirectDate;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/dashboard";
        }
    }
}
