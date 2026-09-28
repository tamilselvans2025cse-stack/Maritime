package com.example.maritime.controller;

import com.example.maritime.model.PurchaseOrder;
import com.example.maritime.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class PurchaseOrderRestController {

    private final PurchaseService purchaseService;

    @GetMapping("/purchase-orders")
    public List<PurchaseOrder> getAll() {
        return purchaseService.findAllOrders();
    }

    @PostMapping("/purchase-orders")
    public PurchaseOrder create(@Valid @RequestBody PurchaseOrder order) {
        return purchaseService.createOrder(order);
    }

    @PostMapping("/purchase-orders/{id}/approve")
    public PurchaseOrder approve(@PathVariable Long id) {
        return purchaseService.approveOrder(id);
    }
}
