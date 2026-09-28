package com.example.maritime.controller;

import com.example.maritime.model.Contact;
import com.example.maritime.model.ContactType;
import com.example.maritime.model.Product;
import com.example.maritime.model.PurchaseOrder;
import com.example.maritime.model.PurchaseOrderLine;
import com.example.maritime.repository.ContactRepository;
import com.example.maritime.repository.ProductRepository;
import com.example.maritime.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseService purchaseService;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;

    @GetMapping("/purchase-orders")
    public String purchaseOrders(Model model) {
        model.addAttribute("orders", purchaseService.findAllOrders());
        model.addAttribute("vendors", contactRepository.findAll().stream()
                .filter(c -> c.getContactType() == ContactType.VENDOR).toList());
        model.addAttribute("products", productRepository.findAll());
        return "purchase-orders";
    }

    @PostMapping("/purchase-orders")
    public String createOrder(@RequestParam Long vendorId, @RequestParam Long productId,
                               @RequestParam Double quantity, @RequestParam BigDecimal unitPrice) {
        Contact vendor = contactRepository.findById(vendorId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        PurchaseOrder order = PurchaseOrder.builder()
                .vendor(vendor)
                .totalAmount(lineTotal)
                .build();
        List<PurchaseOrderLine> lines = new ArrayList<>();
        lines.add(PurchaseOrderLine.builder().purchaseOrder(order).product(product)
                .quantity(quantity).unitPrice(unitPrice).lineTotal(lineTotal).build());
        order.setLines(lines);

        purchaseService.createOrder(order);
        return "redirect:/purchase-orders";
    }

    @PostMapping("/purchase-orders/{id}/approve")
    public String approve(@PathVariable Long id) {
        purchaseService.approveOrder(id);
        return "redirect:/purchase-orders";
    }
}
