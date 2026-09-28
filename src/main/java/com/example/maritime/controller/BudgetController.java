package com.example.maritime.controller;

import com.example.maritime.model.Budget;
import com.example.maritime.repository.AnalyticAccountRepository;
import com.example.maritime.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final AnalyticAccountRepository analyticAccountRepository;

    @GetMapping("/budgets")
    public String budgets(Model model) {
        model.addAttribute("budgets", budgetService.findAll());
        model.addAttribute("analyticAccounts", analyticAccountRepository.findAll());
        return "budgets";
    }

    @PostMapping("/budgets")
    public String createBudget(@RequestParam String budgetName, @RequestParam Long analyticAccountId,
                                @RequestParam String fiscalYear, @RequestParam java.math.BigDecimal totalBudget) {
        var analyticAccount = analyticAccountRepository.findById(analyticAccountId).orElseThrow();
        Budget budget = Budget.builder()
                .budgetName(budgetName).analyticAccount(analyticAccount)
                .fiscalYear(fiscalYear).totalBudget(totalBudget)
                .build();
        budgetService.save(budget);
        return "redirect:/budgets";
    }
}
