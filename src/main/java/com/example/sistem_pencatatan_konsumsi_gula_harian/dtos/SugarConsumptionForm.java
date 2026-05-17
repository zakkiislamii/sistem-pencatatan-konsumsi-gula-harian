package com.example.sistem_pencatatan_konsumsi_gula_harian.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SugarConsumptionForm {

    private Integer consumptionId;

    @NotNull(message = "Jumlah konsumsi wajib diisi")
    @DecimalMin(value = "0.0", message = "Jumlah harus >= 0")
    private BigDecimal amount;

    @NotNull(message = "Deskripsi wajib diisi")
    private String description;

    @NotNull(message = "Tanggal dan waktu wajib diisi")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @PastOrPresent(message = "Tanggal tidak boleh di masa depan")
    private LocalDateTime consumedAt;
}
