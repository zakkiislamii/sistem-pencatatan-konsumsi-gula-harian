package com.example.sistem_pencatatan_konsumsi_gula_harian.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;

public interface SugarConsumptionRepository extends JpaRepository<SugarConsumption, Integer> {

    List<SugarConsumption> findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(User user, LocalDateTime start, LocalDateTime end);

    Optional<SugarConsumption> findByConsumptionIdAndUser(Integer consumptionId, User user);

}
