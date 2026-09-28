package com.example.maritime.controller;

import com.example.maritime.model.CustomerInvoice;
import com.example.maritime.repository.CustomerInvoiceRepository;
import com.example.maritime.service.PaymentService;
import com.example.maritime.service.SalesService;
import com.example.maritime.dto.PaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class CustomerInvoiceRestController {

    private final SalesService salesService;
    private final PaymentService paymentService;
    private final CustomerInvoiceRepository customerInvoiceRepository;

    @GetMapping("/invoices")
    public List<CustomerInvoice> getAll() {
        return salesService.findAllInvoices();
    }

    @PostMapping("/invoices")
    public CustomerInvoice create(@RequestBody CustomerInvoice invoice) {
        // Manual invoice creation (outside the normal docking-driven auto-generation flow).
        return customerInvoiceRepository.save(invoice);
    }

    @PostMapping("/invoices/{id}/pay")
    public Object pay(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        request.setInvoiceId(id);
        return paymentService.recordCustomerPayment(request);
    }
}
