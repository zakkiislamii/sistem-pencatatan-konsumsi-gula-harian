package com.example.sistem_pencatatan_konsumsi_gula_harian.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;

public interface SugarConsumptionRepository extends JpaRepository<SugarConsumption, Integer> {
}
