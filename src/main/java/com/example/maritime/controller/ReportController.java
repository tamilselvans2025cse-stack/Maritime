package com.example.maritime.controller;

import com.example.maritime.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/balance-sheet")
    public String balanceSheet(Model model) {
        var report = reportService.balanceSheet();
        model.addAttribute("assets", report.get("assets"));
        model.addAttribute("liabilities", report.get("liabilities"));
        model.addAttribute("totalAssets", report.get("totalAssets"));
        model.addAttribute("totalLiabilities", report.get("totalLiabilities"));
        return "balance-sheet";
    }

    @GetMapping("/profit-loss")
    public String profitLoss(Model model) {
        var report = reportService.profitAndLoss();
        model.addAttribute("income", report.get("income"));
        model.addAttribute("expenses", report.get("expenses"));
        model.addAttribute("totalRevenue", report.get("totalRevenue"));
        model.addAttribute("totalExpenses", report.get("totalExpenses"));
        model.addAttribute("netProfit", report.get("netProfit"));
        return "profit-loss";
    }

    @GetMapping("/budget-report")
    public String budgetReport(Model model) {
        model.addAttribute("rows", reportService.budgetReport());
        return "budget-report";
    }
}
