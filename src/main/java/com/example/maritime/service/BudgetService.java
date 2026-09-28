package com.example.maritime.service;

import com.example.maritime.exception.ResourceNotFoundException;
import com.example.maritime.model.Budget;
import com.example.maritime.model.BudgetLine;
import com.example.maritime.repository.BudgetLineRepository;
import com.example.maritime.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Section 27-28: budgets are planned amounts per (analytic account, account)
 * pair. Variance = Actual - Planned. Utilization % = Actual / Planned * 100.
 */
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetLineRepository budgetLineRepository;

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }
    
    public Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id " + id));
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    /** Recomputes variance for every line of a budget from its actualAmount. */
    public Budget recalculateVariance(Long budgetId) {
        Budget budget = findById(budgetId);
        for (BudgetLine line : budget.getLines()) {
            BigDecimal planned = line.getPlannedAmount() != null ? line.getPlannedAmount() : BigDecimal.ZERO;
            BigDecimal actual = line.getActualAmount() != null ? line.getActualAmount() : BigDecimal.ZERO;
            line.setVariance(actual.subtract(planned));
            budgetLineRepository.save(line);
        }
        return budget;
    }

    public List<Budget> findByAnalyticAccount(Long analyticAccountId) {
        return budgetRepository.findAll().stream()
                .filter(b -> b.getAnalyticAccount() != null && b.getAnalyticAccount().getId().equals(analyticAccountId))
                .toList();
    }
}
