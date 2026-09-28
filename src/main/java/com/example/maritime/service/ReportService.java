package com.example.maritime.service;

import com.example.maritime.model.Account;
import com.example.maritime.model.AccountType;
import com.example.maritime.model.Budget;
import com.example.maritime.model.BudgetLine;
import com.example.maritime.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the three financial reports (section 29) from the chart of
 * accounts' running balances and the budget lines.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final AccountRepository accountRepository;
    private final BudgetService budgetService;

    public Map<String, Object> balanceSheet() {
        List<Account> assets = filterByType(AccountType.ASSET);
        List<Account> liabilities = filterByType(AccountType.LIABILITY);

        BigDecimal totalAssets = sumBalances(assets);
        BigDecimal totalLiabilities = sumBalances(liabilities);

        Map<String, Object> report = new HashMap<>();
        report.put("assets", assets);
        report.put("liabilities", liabilities);
        report.put("totalAssets", totalAssets);
        report.put("totalLiabilities", totalLiabilities);
        return report;
    }

    public Map<String, Object> profitAndLoss() {
        List<Account> income = filterByType(AccountType.INCOME);
        List<Account> expenses = filterByType(AccountType.EXPENSE);

        // Income accounts are credited (balance goes negative in our simplified
        // debit-increases model), so flip sign for display.
        BigDecimal totalRevenue = sumBalances(income).negate();
        BigDecimal totalExpenses = sumBalances(expenses);
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        Map<String, Object> report = new HashMap<>();
        report.put("income", income);
        report.put("expenses", expenses);
        report.put("totalRevenue", totalRevenue);
        report.put("totalExpenses", totalExpenses);
        report.put("netProfit", netProfit);
        return report;
    }

    public List<Map<String, Object>> budgetReport() {
        List<Budget> budgets = budgetService.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Budget budget : budgets) {
            for (BudgetLine line : budget.getLines()) {
                Map<String, Object> row = new HashMap<>();
                row.put("analyticAccount", budget.getAnalyticAccount() != null ? budget.getAnalyticAccount().getName() : "");
                row.put("account", line.getAccount() != null ? line.getAccount().getAccountName() : "");
                BigDecimal planned = line.getPlannedAmount() != null ? line.getPlannedAmount() : BigDecimal.ZERO;
                BigDecimal actual = line.getActualAmount() != null ? line.getActualAmount() : BigDecimal.ZERO;
                BigDecimal variance = actual.subtract(planned);
                double utilization = planned.compareTo(BigDecimal.ZERO) == 0 ? 0
                        : actual.divide(planned, 4, RoundingMode.HALF_UP).doubleValue() * 100;
                row.put("planned", planned);
                row.put("actual", actual);
                row.put("variance", variance);
                row.put("utilizationPercent", Math.round(utilization * 100.0) / 100.0);
                rows.add(row);
            }
        }
        return rows;
    }

    public Map<String, Object> budgetVarianceForAnalyticAccount(Long analyticAccountId) {
        List<Budget> budgets = budgetService.findByAnalyticAccount(analyticAccountId);
        BigDecimal totalPlanned = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        for (Budget b : budgets) {
            for (BudgetLine line : b.getLines()) {
                totalPlanned = totalPlanned.add(line.getPlannedAmount() != null ? line.getPlannedAmount() : BigDecimal.ZERO);
                totalActual = totalActual.add(line.getActualAmount() != null ? line.getActualAmount() : BigDecimal.ZERO);
            }
        }
        BigDecimal variance = totalActual.subtract(totalPlanned);
        double utilization = totalPlanned.compareTo(BigDecimal.ZERO) == 0 ? 0
                : totalActual.divide(totalPlanned, 4, RoundingMode.HALF_UP).doubleValue() * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("plannedAmount", totalPlanned);
        result.put("actualAmount", totalActual);
        result.put("variance", variance);
        result.put("utilizationPercentage", Math.round(utilization * 100.0) / 100.0);
        return result;
    }

    private List<Account> filterByType(AccountType type) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getAccountType() == type)
                .toList();
    }

    private BigDecimal sumBalances(List<Account> accounts) {
        return accounts.stream()
                .map(Account::getBalance)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
