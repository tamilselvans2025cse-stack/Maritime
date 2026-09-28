package com.example.maritime.repository;

import com.example.maritime.model.BudgetLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetLineRepository extends JpaRepository<BudgetLine, Long> {
}
