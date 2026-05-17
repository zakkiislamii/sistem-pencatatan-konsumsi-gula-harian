package com.example.sistem_pencatatan_konsumsi_gula_harian;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.sistem_pencatatan_konsumsi_gula_harian.dtos.SugarConsumptionForm;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;
import com.example.sistem_pencatatan_konsumsi_gula_harian.repositories.SugarConsumptionRepository;
import com.example.sistem_pencatatan_konsumsi_gula_harian.services.SugarConsumptionService;

public class SugarConsumptionServiceUnitTests {

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
        u.setUsername("user");
        return u;
    }

    private SugarConsumptionForm makeForm(BigDecimal amount, LocalDateTime consumedAt) {
        SugarConsumptionForm f = new SugarConsumptionForm();
        f.setAmount(amount);
        f.setDescription("desc");
        f.setConsumedAt(consumedAt);
        return f;
    }

    private SugarConsumption makeEntity(BigDecimal amount, LocalDateTime consumedAt, User user) {
        SugarConsumption s = new SugarConsumption();
        s.setConsumptionId(1);
        s.setAmount(amount);
        s.setDescription("desc");
        s.setConsumedAt(consumedAt);
        s.setUser(user);
        return s;
    }

    @Test
    void addConsumption_success() {
        User user = makeUser();
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        SugarConsumptionForm form = makeForm(new BigDecimal("10"), now);

        when(repository.save(any(SugarConsumption.class))).thenAnswer(inv -> {
            SugarConsumption arg = inv.getArgument(0);
            arg.setConsumptionId(5);
            return arg;
        });

        SugarConsumption saved = service.addConsumption(form, user);

        assertNotNull(saved);
        assertEquals(new BigDecimal("10"), saved.getAmount());
        assertEquals(user, saved.getUser());
        verify(repository, times(1)).save(any(SugarConsumption.class));
    }

    @Test
    void addConsumption_validateAmountNull() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(null, LocalDateTime.now());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addConsumption(form, user));
        assertEquals("Jumlah konsumsi wajib diisi", ex.getMessage());
    }

    @Test
    void addConsumption_validateAmountNegative() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("-1"), LocalDateTime.now());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addConsumption(form, user));
        assertEquals("Jumlah harus >= 0", ex.getMessage());
    }

    @Test
    void addConsumption_validateConsumedAtNull() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("5"), null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()
                -> service.addConsumption(form, user));
        assertEquals("Tanggal dan waktu konsumsi wajib diisi", ex.getMessage());
    }

    @Test
    void addConsumption_validateConsumedAtFuture() {
        User user = makeUser();
        SugarConsumptionForm form = makeForm(new BigDecimal("5"), LocalDateTime.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()
                -> service.addConsumption(form, user));
        assertEquals("Tanggal tidak boleh di masa depan", ex.getMessage());
    }

    @Test
    void updateConsumption_success() {
        User user = makeUser();
        Integer id = 2;
        LocalDateTime consumedAt = LocalDateTime.now().minusHours(2);
        SugarConsumption existing = makeEntity(new BigDecimal("4"), consumedAt.minusHours(1), user);
        SugarConsumptionForm form = makeForm(new BigDecimal("7"), consumedAt);

        when(repository.findByConsumptionIdAndUser(eq(id), eq(user))).thenReturn(Optional.of(existing));
        when(repository.save(any(SugarConsumption.class))).thenAnswer(i -> i.getArgument(0));

        SugarConsumption updated = service.updateConsumption(id, form, user);

        assertEquals(new BigDecimal("7"), updated.getAmount());
        assertEquals(consumedAt, updated.getConsumedAt());
        verify(repository).findByConsumptionIdAndUser(eq(id), eq(user));
        verify(repository).save(existing);
    }

    @Test
    void updateConsumption_notFound() {
        User user = makeUser();
        Integer id = 99;
        SugarConsumptionForm form = makeForm(new BigDecimal("5"), LocalDateTime.now());

        when(repository.findByConsumptionIdAndUser(eq(id), eq(user))).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()
                -> service.updateConsumption(id, form, user));
        assertEquals("Data tidak ditemukan", ex.getMessage());
    }

    @Test
    void getTotalForDate_emptyReturnsZero() {
        User user = makeUser();
        LocalDate d = LocalDate.now();

        when(repository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(eq(user), any(), any()))
                .thenReturn(Collections.emptyList());

        BigDecimal total = service.getTotalForDate(user, d);
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void getTotalForDate_sumsAmounts() {
        User user = makeUser();
        LocalDate d = LocalDate.now();
        LocalDateTime start = d.atStartOfDay();
        LocalDateTime end = d.atTime(LocalTime.MAX);

        SugarConsumption s1 = makeEntity(new BigDecimal("10"), start.plusHours(1), user);
        SugarConsumption s2 = makeEntity(new BigDecimal("15"), start.plusHours(2), user);
        SugarConsumption s3 = makeEntity(null, start.plusHours(3), user);

        when(repository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(eq(user), eq(start), eq(end)))
                .thenReturn(Arrays.asList(s1, s2, s3));

        BigDecimal total = service.getTotalForDate(user, d);
        assertEquals(new BigDecimal("25"), total);
    }

    @Test
    void deleteConsumption_success() {
        User user = makeUser();
        Integer id = 3;
        SugarConsumption s = makeEntity(new BigDecimal("5"), LocalDateTime.now().minusDays(1), user);

        when(repository.findByConsumptionIdAndUser(eq(id), eq(user))).thenReturn(Optional.of(s));

        service.deleteConsumption(id, user);

        verify(repository).delete(s);
    }

    @Test
    void deleteConsumption_notFound() {
        User user = makeUser();
        Integer id = 999;

        when(repository.findByConsumptionIdAndUser(eq(id), eq(user))).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()
                -> service.deleteConsumption(id, user));
        assertEquals("Data tidak ditemukan", ex.getMessage());
    }

    @Test
    void getDailyStatus_thresholdBehavior() {
        assertEquals("normal", service.getDailyStatus(new BigDecimal("50")));
        assertEquals("normal", service.getDailyStatus(new BigDecimal("10")));
        assertEquals("melebihi batas konsumsi", service.getDailyStatus(new BigDecimal("51")));
    }

    @Test
    void findByIdForUser_found() {
        User user = makeUser();
        SugarConsumption entity = makeEntity(new BigDecimal("10"), LocalDateTime.now().minusHours(1), user);

        when(repository.findByConsumptionIdAndUser(eq(1), eq(user)))
                .thenReturn(Optional.of(entity));

        Optional<SugarConsumption> result = service.findByIdForUser(1, user);

        assertEquals(Optional.of(entity), result);
    }

    @Test
    void findByIdForUser_notFound() {
        User user = makeUser();

        when(repository.findByConsumptionIdAndUser(eq(99), eq(user)))
                .thenReturn(Optional.empty());

        Optional<SugarConsumption> result = service.findByIdForUser(99, user);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void getConsumptionsForDate_returnsListForDate() {
        User user = makeUser();
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        SugarConsumption s1 = makeEntity(new BigDecimal("10"), start.plusHours(1), user);
        SugarConsumption s2 = makeEntity(new BigDecimal("20"), start.plusHours(2), user);

        when(repository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(eq(user), eq(start), eq(end)))
                .thenReturn(Arrays.asList(s1, s2));

        List<SugarConsumption> result = service.getConsumptionsForDate(user, date);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("10"), result.get(0).getAmount());
    }
}
