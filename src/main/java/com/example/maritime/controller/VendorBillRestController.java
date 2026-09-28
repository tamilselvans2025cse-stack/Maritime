package com.example.maritime.controller;

import com.example.maritime.model.VendorBill;
import com.example.maritime.service.PaymentService;
import com.example.maritime.service.PurchaseService;
import com.example.maritime.dto.PaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class VendorBillRestController {

    private final PurchaseService purchaseService;
    private final PaymentService paymentService;

    @GetMapping("/vendor-bills")
    public List<VendorBill> getAll() {
        return purchaseService.findAllBills();
    }

    @PostMapping("/vendor-bills")
    public VendorBill create(@RequestBody Map<String, Object> body) {
        Long poId = Long.valueOf(body.get("purchaseOrderId").toString());
        return purchaseService.createBillFromOrder(poId);
    }

    @PostMapping("/vendor-bills/{id}/pay")
    public Object pay(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        request.setVendorBillId(id);
        return paymentService.recordVendorPayment(request);
    }
}
