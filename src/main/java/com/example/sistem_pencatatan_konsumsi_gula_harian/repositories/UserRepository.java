package com.example.sistem_pencatatan_konsumsi_gula_harian.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
