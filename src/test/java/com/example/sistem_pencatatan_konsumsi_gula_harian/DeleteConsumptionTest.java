package com.example.sistem_pencatatan_konsumsi_gula_harian;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.repositories.SugarConsumptionRepository;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.SugarConsumptionService;

public class DeleteConsumptionTest {

    @Mock
    private SugarConsumptionRepository repository;

    private SugarConsumptionService service;

    // Setup mock dan service sebelum tiap test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SugarConsumptionService(repository);
    }

    // Helper untuk membuat dummy user
    private User makeUser() {
        User u = new User();
        u.setUserId(1);
        u.setUsername("testuser");
        u.setName("Test User");
        return u;
    }

    // Helper untuk membuat dummy data konsumsi
    private SugarConsumption makeEntity(User user) {
        SugarConsumption sc = new SugarConsumption();
        sc.setConsumptionId(1);
        sc.setAmount(new BigDecimal("10"));
        sc.setDescription("Test konsumsi");
        sc.setConsumedAt(LocalDateTime.now().minusDays(1));
        sc.setUser(user);
        return sc;
    }

    // P1: data tidak ditemukan -> harus error
    @Test
    void deleteConsumption_P1_dataNotFound() {
        User user = makeUser();

        // Mock pencarian data -> hasilnya kosong
        when(repository.findByConsumptionIdAndUser(eq(99), eq(user)))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.deleteConsumption(99, user));

        assertEquals("Data tidak ditemukan", ex.getMessage());
    }

    // P2: data ditemukan -> berhasil dihapus
    @Test
    void deleteConsumption_P2_sukses() {
        User user = makeUser();
        SugarConsumption existing = makeEntity(user);

        // Mock pencarian data -> data ditemukan
        when(repository.findByConsumptionIdAndUser(eq(1), eq(user)))
                .thenReturn(Optional.of(existing));

        service.deleteConsumption(1, user);

        // Pastikan delete() dipanggil
        verify(repository).delete(existing);
    }
}
