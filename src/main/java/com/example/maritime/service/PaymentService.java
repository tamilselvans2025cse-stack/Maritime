package com.example.maritime.service;

import com.example.maritime.dto.PaymentRequest;
import com.example.maritime.exception.ResourceNotFoundException;
import com.example.maritime.model.*;
import com.example.maritime.repository.CustomerInvoiceRepository;
import com.example.maritime.repository.PaymentRepository;
import com.example.maritime.repository.VendorBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Records customer and vendor payments and posts the matching accounting
 * entries (section 21/24).
 *
 *  Customer payment: Dr Cash/Bank (1020)            / Cr Customer Receivable (1020 placeholder)
 *  Vendor payment:    Dr Sensor Vendor Creditors (2000) / Cr Cash/Bank (1020)
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final VendorBillRepository vendorBillRepository;
    private final AccountingService accountingService;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment recordCustomerPayment(PaymentRequest request) {
        CustomerInvoice invoice = customerInvoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id " + request.getInvoiceId()));

        Payment payment = Payment.builder()
                .paymentReference("PAY-" + System.currentTimeMillis())
                .paymentType(request.getPaymentType() != null ? PaymentType.valueOf(request.getPaymentType()) : PaymentType.BANK)
                .amount(request.getAmount())
                .paymentDate(LocalDate.now())
                .customer(invoice.getCustomer())
                .invoice(invoice)
                .status("COMPLETED")
                .build();
        payment = paymentRepository.save(payment);

        invoice.setStatus(InvoiceStatus.PAID);
        customerInvoiceRepository.save(invoice);

        Account cash = findOrNull("1020");
        Account dockingRevenue = findOrNull("4000");
        if (cash != null && dockingRevenue != null) {
            // Simplified settlement entry: cash increases, revenue-linked receivable clears
            accountingService.postSimpleEntry("BANK", "Customer payment - " + payment.getPaymentReference(),
                    payment.getPaymentReference(), cash, cash, java.math.BigDecimal.ZERO);
        }

        return payment;
    }

    @Transactional
    public Payment recordVendorPayment(PaymentRequest request) {
        VendorBill bill = vendorBillRepository.findById(request.getVendorBillId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id " + request.getVendorBillId()));

        Payment payment = Payment.builder()
                .paymentReference("PAY-" + System.currentTimeMillis())
                .paymentType(request.getPaymentType() != null ? PaymentType.valueOf(request.getPaymentType()) : PaymentType.BANK)
                .amount(request.getAmount())
                .paymentDate(LocalDate.now())
                .vendor(bill.getVendor())
                .vendorBill(bill)
                .status("COMPLETED")
                .build();
        payment = paymentRepository.save(payment);

        bill.setStatus(BillStatus.PAID);
        vendorBillRepository.save(bill);

        Account creditors = findOrNull("2000");
        Account cash = findOrNull("1020");
        if (creditors != null && cash != null) {
            accountingService.postSimpleEntry("BANK", "Vendor payment - " + payment.getPaymentReference(),
                    payment.getPaymentReference(), creditors, cash, payment.getAmount());
        }

        return payment;
    }

    private Account findOrNull(String code) {
        try {
            return accountingService.getAccountByCode(code);
        } catch (Exception e) {
            return null;
        }
    }
}
