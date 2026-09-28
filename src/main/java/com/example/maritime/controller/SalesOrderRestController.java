package com.example.maritime.controller;

import com.example.maritime.model.SalesOrder;
import com.example.maritime.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class SalesOrderRestController {

    private final SalesService salesService;

    @GetMapping("/sales-orders")
    public List<SalesOrder> getAll() {
        return salesService.findAllOrders();
    }

    @PostMapping("/sales-orders")
    public SalesOrder create(@Valid @RequestBody SalesOrder order) {
        return salesService.createOrder(order);
    }
}
