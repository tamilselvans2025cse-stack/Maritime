package com.example.maritime.repository;

import com.example.maritime.model.Berth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BerthRepository extends JpaRepository<Berth, Long> {
}
