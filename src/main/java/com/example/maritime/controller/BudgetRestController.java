package com.example.maritime.controller;

import com.example.maritime.model.Budget;
import com.example.maritime.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class BudgetRestController {

    private final BudgetService budgetService;

    @GetMapping("/budgets")
    public List<Budget> getAll() {
        return budgetService.findAll();
    }

    @PostMapping("/budgets")
    public Budget create(@Valid @RequestBody Budget budget) {
        return budgetService.save(budget);
    }
}
