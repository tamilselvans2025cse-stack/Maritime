package com.example.maritime.service;

import com.example.maritime.exception.ResourceNotFoundException;
import com.example.maritime.model.*;
import com.example.maritime.repository.PurchaseOrderRepository;
import com.example.maritime.repository.VendorBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles vendor-side procurement: purchase orders, converting an approved
 * PO into a vendor bill, and the accounting entries that expense generates.
 */
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorBillRepository vendorBillRepository;
    private final AccountingService accountingService;

    public List<PurchaseOrder> findAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder findOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id " + id));
    }

    public PurchaseOrder createOrder(PurchaseOrder order) {
        if (order.getPoNumber() == null) {
            order.setPoNumber("PO-" + System.currentTimeMillis());
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        order.setStatus(POStatus.DRAFT);
        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder approveOrder(Long id) {
        PurchaseOrder order = findOrderById(id);
        order.setStatus(POStatus.APPROVED);
        return purchaseOrderRepository.save(order);
    }

    public List<VendorBill> findAllBills() {
        return vendorBillRepository.findAll();
    }

    public VendorBill findBillById(Long id) {
        return vendorBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id " + id));
    }

    /**
     * Section 22-23: Purchase Order -> Vendor Bill.
     * Posts: Dr Mooring Winch Maintenance (5000) / Cr Sensor Vendor Creditors (2000)
     */
    @Transactional
    public VendorBill createBillFromOrder(Long purchaseOrderId) {
        PurchaseOrder order = findOrderById(purchaseOrderId);
        if (order.getStatus() != POStatus.APPROVED && order.getStatus() != POStatus.RECEIVED) {
            throw new IllegalStateException("Purchase order must be APPROVED or RECEIVED before billing");
        }

        VendorBill bill = VendorBill.builder()
                .billNumber("BILL-" + System.currentTimeMillis())
                .vendor(order.getVendor())
                .purchaseOrder(order)
                .billDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .amount(order.getTotalAmount())
                .status(BillStatus.OPEN)
                .build();
        bill = vendorBillRepository.save(bill);

        order.setStatus(POStatus.BILLED);
        purchaseOrderRepository.save(order);

        Account expenseAccount = findOrNull("5000");
        Account creditorsAccount = findOrNull("2000");
        if (expenseAccount != null && creditorsAccount != null && bill.getAmount() != null
                && bill.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            accountingService.postSimpleEntry("PURCHASE", "Vendor bill - " + bill.getBillNumber(),
                    bill.getBillNumber(), expenseAccount, creditorsAccount, bill.getAmount());
        }

        return bill;
    }

    private Account findOrNull(String code) {
        try {
            return accountingService.getAccountByCode(code);
        } catch (Exception e) {
            return null;
        }
    }
}
