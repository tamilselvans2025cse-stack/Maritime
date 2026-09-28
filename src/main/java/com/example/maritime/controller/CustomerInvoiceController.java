package com.example.maritime.controller;

import com.example.maritime.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CustomerInvoiceController {

    private final SalesService salesService;

    @GetMapping("/invoices")
    public String invoices(Model model) {
        model.addAttribute("invoices", salesService.findAllInvoices());
        return "invoices";
    }

    @PostMapping("/invoices/{id}/pay")
    public String markPaid(@PathVariable Long id) {
        salesService.markInvoicePaid(id);
        return "redirect:/invoices";
    }
}
