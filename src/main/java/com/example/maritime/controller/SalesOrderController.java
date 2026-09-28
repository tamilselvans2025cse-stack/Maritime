package com.example.maritime.controller;

import com.example.maritime.model.*;
import com.example.maritime.repository.ContactRepository;
import com.example.maritime.repository.VesselRepository;
import com.example.maritime.repository.BerthRepository;
import com.example.maritime.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesService salesService;
    private final ContactRepository contactRepository;
    private final VesselRepository vesselRepository;
    private final BerthRepository berthRepository;

    @GetMapping("/sales-orders")
    public String salesOrders(Model model) {
        model.addAttribute("orders", salesService.findAllOrders());
        model.addAttribute("customers", contactRepository.findAll().stream()
                .filter(c -> c.getContactType() == ContactType.CUSTOMER).toList());
        model.addAttribute("vessels", vesselRepository.findAll());
        model.addAttribute("berths", berthRepository.findAll());
        return "sales-orders";
    }

    @PostMapping("/sales-orders")
    public String createOrder(@RequestParam Long customerId, @RequestParam Long vesselId,
                               @RequestParam Long berthId, @RequestParam(required = false) java.math.BigDecimal totalAmount) {
        Contact customer = contactRepository.findById(customerId).orElseThrow();
        Vessel vessel = vesselRepository.findById(vesselId).orElseThrow();
        Berth berth = berthRepository.findById(berthId).orElseThrow();

        SalesOrder order = SalesOrder.builder()
                .customer(customer).vessel(vessel).berth(berth)
                .totalAmount(totalAmount)
                .status(SalesOrderStatus.CONFIRMED)
                .build();
        salesService.createOrder(order);
        return "redirect:/sales-orders";
    }
}
