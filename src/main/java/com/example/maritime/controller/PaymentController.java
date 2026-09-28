package com.example.maritime.controller;

import com.example.maritime.dto.PaymentRequest;
import com.example.maritime.service.PaymentService;
import com.example.maritime.service.PurchaseService;
import com.example.maritime.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SalesService salesService;
    private final PurchaseService purchaseService;

    @GetMapping("/payments")
    public String payments(Model model) {
        model.addAttribute("payments", paymentService.findAll());
        model.addAttribute("invoices", salesService.findAllInvoices());
        model.addAttribute("bills", purchaseService.findAllBills());
        return "payments";
    }

    @PostMapping("/payments/customer")
    public String payCustomerInvoice(@RequestParam Long invoiceId, @RequestParam BigDecimal amount,
                                      @RequestParam(required = false, defaultValue = "BANK") String paymentType) {
        PaymentRequest request = new PaymentRequest();
        request.setInvoiceId(invoiceId);
        request.setAmount(amount);
        request.setPaymentType(paymentType);
        paymentService.recordCustomerPayment(request);
        return "redirect:/payments";
    }

    @PostMapping("/payments/vendor")
    public String payVendorBill(@RequestParam Long vendorBillId, @RequestParam BigDecimal amount,
                                 @RequestParam(required = false, defaultValue = "BANK") String paymentType) {
        PaymentRequest request = new PaymentRequest();
        request.setVendorBillId(vendorBillId);
        request.setAmount(amount);
        request.setPaymentType(paymentType);
        paymentService.recordVendorPayment(request);
        return "redirect:/payments";
    }
}
