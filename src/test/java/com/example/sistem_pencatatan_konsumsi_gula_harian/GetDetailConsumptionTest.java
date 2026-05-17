package com.example.sistem_pencatatan_konsumsi_gula_harian;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.DailyConsumptionDetail;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.enums.ConsumptionStatus;
import com.example.sistem_pencatatan_konsumsi_gula_harian.repositories.SugarConsumptionRepository;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.SugarConsumptionService;

public class GetDetailConsumptionTest {

    @Mock
    private SugarConsumptionRepository repository;

    private SugarConsumptionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SugarConsumptionService(repository);
    }

    private User makeUser() {
        User u = new User();
        u.setUserId(1);
        u.setUsername("testuser");
        u.setName("Test User");
        return u;
    }

    private SugarConsumption makeEntity(BigDecimal amount, User user) {
        SugarConsumption sc = new SugarConsumption();
        sc.setConsumptionId(1);
        sc.setAmount(amount);
        sc.setDescription("Test konsumsi");
        sc.setConsumedAt(LocalDateTime.now().minusHours(1));
        sc.setUser(user);
        return sc;
    }

    // P1: consumptions kosong → return early
    @Test
    void getDetailConsumption_TC1_dataKosong() {
        User user = makeUser();
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        when(repository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(
                eq(user), eq(start), eq(end)))
                .thenReturn(Collections.emptyList());

        DailyConsumptionDetail result = service.getDetailConsumption(user, date);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getDailyTotal());
        assertNull(result.getDailyStatus());   
        assertEquals(0, result.getConsumptions().size());
        assertTrue(result.isEmpty());          
    }

    // P2: total <= 50, status NORMAL
    @Test
    void getDetailConsumption_TC2_statusNormal() {
        User user = makeUser();
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        SugarConsumption s1 = makeEntity(new BigDecimal("10"), user);
        SugarConsumption s2 = makeEntity(new BigDecimal("20"), user);

        when(repository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(
                eq(user), eq(start), eq(end)))
                .thenReturn(Arrays.asList(s1, s2));

        DailyConsumptionDetail result = service.getDetailConsumption(user, date);

        assertNotNull(result);
        assertEquals(new BigDecimal("30"), result.getDailyTotal());
        assertEquals(ConsumptionStatus.NORMAL, result.getDailyStatus());
        assertEquals(2, result.getConsumptions().size());
        assertFalse(result.isEmpty());          
    }

    // P3: total > 50, status MELEBIHI_BATAS
    @Test
    void getDetailConsumption_TC3_statusMelebihiBatas() {
        User user = makeUser();
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        SugarConsumption s1 = makeEntity(new BigDecimal("30"), user);
        SugarConsumption s2 = makeEntity(new BigDecimal("25"), user);

        when(repository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(
                eq(user), eq(start), eq(end)))
                .thenReturn(Arrays.asList(s1, s2));

        DailyConsumptionDetail result = service.getDetailConsumption(user, date);

        assertNotNull(result);
        assertEquals(new BigDecimal("55"), result.getDailyTotal());
        assertEquals(ConsumptionStatus.MELEBIHI_BATAS, result.getDailyStatus());
        assertEquals(2, result.getConsumptions().size());
        assertFalse(result.isEmpty());         
    }
}
