package com.example.sistem_pencatatan_konsumsi_gula_harian;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.SugarConsumptionForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.repositories.SugarConsumptionRepository;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.SugarConsumptionService;

public class UpdateConsumptionTest {

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

    private SugarConsumption makeEntity(User user) {
        SugarConsumption sc = new SugarConsumption();
        sc.setConsumptionId(1);
        sc.setAmount(new BigDecimal("5"));
        sc.setDescription("Lama");
        sc.setConsumedAt(LocalDateTime.now().minusDays(1));
        sc.setUser(user);
        return sc;
    }

    //P1: amount == null
    @Test
    void updateConsumption_TC1_amountNull() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(null, LocalDateTime.now().minusMinutes(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateConsumption(1, form, user));

        assertEquals("Jumlah konsumsi wajib diisi", ex.getMessage());
    }

    //P2: amount < 0
    @Test
    void updateConsumption_TC2_amountNegatif() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("-1"), LocalDateTime.now().minusMinutes(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateConsumption(1, form, user));

        assertEquals("Jumlah harus >= 0", ex.getMessage());
    }

    //P3: consumedAt == null
    @Test
    void updateConsumption_TC3_consumedAtNull() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("10"), null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateConsumption(1, form, user));

        assertEquals("Tanggal dan waktu konsumsi wajib diisi", ex.getMessage());
    }

    //P4: consumedAt masa depan
    @Test
    void updateConsumption_TC4_consumedAtMasaDepan() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("10"), LocalDateTime.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateConsumption(1, form, user));

        assertEquals("Tanggal tidak boleh di masa depan", ex.getMessage());
    }

    //P5: semua valid tapi data tidak ditemukan
    @Test
    void updateConsumption_TC5_dataNotFound() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("10"), LocalDateTime.now().minusMinutes(1));

        when(repository.findByConsumptionIdAndUser(eq(99), eq(user)))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateConsumption(99, form, user));

        assertEquals("Data tidak ditemukan", ex.getMessage());
    }

    //P6: semua valid, data ditemukan, berhasil update
    @Test
    void updateConsumption_TC6_sukses() {
        User user = makeUser();
        SugarConsumption existing = makeEntity(user);
        LocalDateTime newTime = LocalDateTime.now().minusMinutes(1);
        SugarConsumptionForm form = makeForm(new BigDecimal("20"), newTime);

        when(repository.findByConsumptionIdAndUser(eq(1), eq(user)))
                .thenReturn(Optional.of(existing));
        when(repository.save(any(SugarConsumption.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        SugarConsumption result = service.updateConsumption(1, form, user);

        assertNotNull(result);
        assertEquals(new BigDecimal("20"), result.getAmount());
        assertEquals("Test konsumsi", result.getDescription());
        assertEquals(newTime, result.getConsumedAt());
        verify(repository).findByConsumptionIdAndUser(eq(1), eq(user));
        verify(repository).save(existing);
    }
}
