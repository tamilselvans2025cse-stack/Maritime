package com.example.maritime.controller;

import com.example.maritime.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class VendorBillController {

    private final PurchaseService purchaseService;

    @GetMapping("/vendor-bills")
    public String vendorBills(Model model) {
        model.addAttribute("bills", purchaseService.findAllBills());
        model.addAttribute("orders", purchaseService.findAllOrders());
        return "vendor-bills";
    }

    @PostMapping("/vendor-bills")
    public String createBill(@RequestParam Long purchaseOrderId) {
        purchaseService.createBillFromOrder(purchaseOrderId);
        return "redirect:/vendor-bills";
    }
}
