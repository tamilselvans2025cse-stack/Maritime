package com.example.maritime.service;

import com.example.maritime.model.*;
import com.example.maritime.repository.CustomerInvoiceRepository;
import com.example.maritime.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles the sales-side of the business: berth-booking sales orders and
 * customer invoices, plus the accounting entries that revenue generates.
 */
@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final AccountingService accountingService;

    public List<SalesOrder> findAllOrders() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder createOrder(SalesOrder order) {
        if (order.getOrderNumber() == null) {
            order.setOrderNumber("SO-" + System.currentTimeMillis());
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        return salesOrderRepository.save(order);
    }

    public List<CustomerInvoice> findAllInvoices() {
        return customerInvoiceRepository.findAll();
    }

    public CustomerInvoice findInvoiceById(Long id) {
        return customerInvoiceRepository.findById(id)
                .orElseThrow(() -> new com.example.maritime.exception.ResourceNotFoundException("Invoice not found with id " + id));
    }

    /**
     * Section 20/25: convert a completed docking event into a customer
     * invoice (docking fee + shore-power fee), then post the matching
     * journal entries:
     *   Dr Customer Receivable / Cr Berth Docking Revenues (docking fee)
     *   Dr Customer Receivable / Cr Shore-Power Sales      (shore-power fee)
     */
    @Transactional
    public CustomerInvoice generateInvoiceFromDocking(DockingEvent dockingEvent) {
        Vessel vessel = dockingEvent.getVessel();
        Contact customer = vessel.getShippingContact();

        CustomerInvoice invoice = CustomerInvoice.builder()
                .invoiceNumber("INV-" + System.currentTimeMillis())
                .customer(customer)
                .vessel(vessel)
                .dockingEvent(dockingEvent)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .subtotal(dockingEvent.getTotalFee())
                .tax(BigDecimal.ZERO)
                .totalAmount(dockingEvent.getTotalFee())
                .status(InvoiceStatus.ISSUED)
                .build();

        invoice = customerInvoiceRepository.save(invoice);

        // Post accounting entries only when a receivable/income chart exists
        // and the fee amounts are non-zero.
        Account receivable = findOrNull("1020"); // simplified: post through Cash/Bank-linked receivable placeholder
        Account dockingRevenue = findOrNull("4000");
        Account shorePowerRevenue = findOrNull("4010");

        if (receivable != null && dockingRevenue != null && dockingEvent.getDockingFee() != null
                && dockingEvent.getDockingFee().compareTo(BigDecimal.ZERO) > 0) {
            accountingService.postSimpleEntry("SALES", "Docking fee revenue - " + invoice.getInvoiceNumber(),
                    invoice.getInvoiceNumber(), receivable, dockingRevenue, dockingEvent.getDockingFee());
        }
        if (receivable != null && shorePowerRevenue != null && dockingEvent.getShorePowerFee() != null
                && dockingEvent.getShorePowerFee().compareTo(BigDecimal.ZERO) > 0) {
            accountingService.postSimpleEntry("SALES", "Shore-power revenue - " + invoice.getInvoiceNumber(),
                    invoice.getInvoiceNumber(), receivable, shorePowerRevenue, dockingEvent.getShorePowerFee());
        }

        return invoice;
    }

    @Transactional
    public CustomerInvoice markInvoicePaid(Long invoiceId) {
        CustomerInvoice invoice = findInvoiceById(invoiceId);
        invoice.setStatus(InvoiceStatus.PAID);
        return customerInvoiceRepository.save(invoice);
    }

    private Account findOrNull(String code) {
        try {
            return accountingService.getAccountByCode(code);
        } catch (Exception e) {
            return null;
        }
    }
}
