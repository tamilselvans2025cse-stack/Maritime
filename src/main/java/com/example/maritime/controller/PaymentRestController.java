package com.example.maritime.controller;

import com.example.maritime.dto.PaymentRequest;
import com.example.maritime.model.Payment;
import com.example.maritime.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime/payments")
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentService paymentService;

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.findAll();
    }

    @PostMapping("/customer")
    public Payment payCustomer(@Valid @RequestBody PaymentRequest request) {
        return paymentService.recordCustomerPayment(request);
    }

    @PostMapping("/vendor")
    public Payment payVendor(@Valid @RequestBody PaymentRequest request) {
        return paymentService.recordVendorPayment(request);
    }
}
