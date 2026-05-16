package com.example.sistem_pencatatan_konsumsi_gula_harian.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sugar_consumptions")
public class SugarConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumption_id")
    private Integer consumptionId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
