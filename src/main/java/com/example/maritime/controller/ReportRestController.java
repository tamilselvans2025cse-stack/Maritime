package com.example.maritime.controller;

import com.example.maritime.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maritime/reports")
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;

    @GetMapping("/balance-sheet")
    public Map<String, Object> balanceSheet() {
        return reportService.balanceSheet();
    }

    @GetMapping("/profit-loss")
    public Map<String, Object> profitLoss() {
        return reportService.profitAndLoss();
    }

    @GetMapping("/budget")
    public List<Map<String, Object>> budget() {
        return reportService.budgetReport();
    }

    @GetMapping("/budget/{analyticAccountId}")
    public Map<String, Object> budgetForAccount(@PathVariable Long analyticAccountId) {
        return reportService.budgetVarianceForAnalyticAccount(analyticAccountId);
    }
}
