package com.example.sistem_pencatatan_konsumsi_gula_harian;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.SugarConsumptionForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.repositories.SugarConsumptionRepository;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.SugarConsumptionService;

public class AddConsumptionTest {

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

    private SugarConsumptionForm makeForm(BigDecimal amount, LocalDateTime consumedAt) {
        SugarConsumptionForm f = new SugarConsumptionForm();
        f.setAmount(amount);
        f.setDescription("Test konsumsi");
        f.setConsumedAt(consumedAt);
        return f;
    }

    //P1: amount == null
    @Test
    void addConsumption_TC1_amountNull() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(null, LocalDateTime.now().minusMinutes(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addConsumption(form, user));

        assertEquals("Jumlah konsumsi wajib diisi", ex.getMessage());
    }

    //P2: amount < 0
    @Test
    void addConsumption_TC2_amountNegatif() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("-1"), LocalDateTime.now().minusMinutes(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addConsumption(form, user));

        assertEquals("Jumlah harus >= 0", ex.getMessage());
    }

    //P3: consumedAt == null
    @Test
    void addConsumption_TC3_consumedAtNull() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("10"), null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addConsumption(form, user));

        assertEquals("Tanggal dan waktu konsumsi wajib diisi", ex.getMessage());
    }

    //P4: consumedAt masa depan
    @Test
    void addConsumption_TC4_consumedAtMasaDepan() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("10"), LocalDateTime.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addConsumption(form, user));

        assertEquals("Tanggal tidak boleh di masa depan", ex.getMessage());
    }

    //P5: semua valid, data tersimpan
    @Test
    void addConsumption_TC5_sukses() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("10"), LocalDateTime.now().minusMinutes(1));

        when(repository.save(any(SugarConsumption.class))).thenAnswer(inv -> {
            SugarConsumption sc = inv.getArgument(0);
            sc.setConsumptionId(1);
            return sc;
        });

        SugarConsumption result = service.addConsumption(form, user);

        assertNotNull(result);
        assertEquals(new BigDecimal("10"), result.getAmount());
        assertEquals(user, result.getUser());
        assertEquals("Test konsumsi", result.getDescription());
        verify(repository).save(any(SugarConsumption.class));
    }
}
