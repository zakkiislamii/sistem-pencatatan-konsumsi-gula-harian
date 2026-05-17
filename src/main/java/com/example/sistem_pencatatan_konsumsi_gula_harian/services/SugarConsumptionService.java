package com.example.sistem_pencatatan_konsumsi_gula_harian.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.DailyConsumptionDetail;
import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.SugarConsumptionForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.repositories.SugarConsumptionRepository;

@Service
public class SugarConsumptionService {

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("50");

    private final SugarConsumptionRepository repository;

    public SugarConsumptionService(SugarConsumptionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SugarConsumption addConsumption(SugarConsumptionForm form, User user) {
        validateForm(form);

        SugarConsumption sc = new SugarConsumption();
        sc.setAmount(form.getAmount());
        sc.setDescription(form.getDescription());
        sc.setConsumedAt(form.getConsumedAt());
        sc.setUser(user);

        return repository.save(sc);
    }

    @Transactional
    public SugarConsumption updateConsumption(Integer id, SugarConsumptionForm form, User user) {
        validateForm(form);

        Optional<SugarConsumption> opt = repository.findByConsumptionIdAndUser(id, user);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Data tidak ditemukan");
        }

        SugarConsumption sc = opt.get();
        sc.setAmount(form.getAmount());
        sc.setDescription(form.getDescription());
        sc.setConsumedAt(form.getConsumedAt());

        return repository.save(sc);
    }

    public Optional<SugarConsumption> findByIdForUser(Integer id, User user) {
        return repository.findByConsumptionIdAndUser(id, user);
    }

    public DailyConsumptionDetail getDetailConsumption(User user, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<SugarConsumption> consumptions = repository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(user, start, end);
        BigDecimal dailyTotal = consumptions.stream()
                .map(SugarConsumption::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String dailyStatus = dailyTotal.compareTo(DAILY_LIMIT) <= 0
                ? "normal"
                : "melebihi batas konsumsi";

        return new DailyConsumptionDetail(consumptions, dailyTotal, dailyStatus);
    }

    @Transactional
    public void deleteConsumption(Integer id, User user) {
        Optional<SugarConsumption> opt = repository.findByConsumptionIdAndUser(id, user);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Data tidak ditemukan");
        }
        repository.delete(opt.get());
    }

    private void validateForm(SugarConsumptionForm form) {
        if (form.getAmount() == null) {
            throw new IllegalArgumentException("Jumlah konsumsi wajib diisi");
        }
        if (form.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Jumlah harus >= 0");
        }
        if (form.getConsumedAt() == null) {
            throw new IllegalArgumentException("Tanggal dan waktu konsumsi wajib diisi");
        }
        if (form.getConsumedAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Tanggal tidak boleh di masa depan");
        }
    }
}
