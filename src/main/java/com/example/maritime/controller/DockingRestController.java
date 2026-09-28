package com.example.maritime.controller;

import com.example.maritime.dto.DockingRequest;
import com.example.maritime.model.DockingEvent;
import com.example.maritime.service.DockingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class DockingRestController {

    private final DockingService dockingService;

    @PostMapping("/dockings")
    public DockingEvent create(@Valid @RequestBody DockingRequest request) {
        return dockingService.createDocking(request);
    }

    @GetMapping("/dockings")
    public List<DockingEvent> getAll() {
        return dockingService.findAll();
    }

    @GetMapping("/dockings/{id}")
    public DockingEvent getOne(@PathVariable Long id) {
        return dockingService.findById(id);
    }

    @PostMapping("/dockings/{id}/departure")
    public DockingEvent depart(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        LocalDateTime departureTime = LocalDateTime.now();
        Double shorePowerKwh = null;
        if (body != null) {
            if (body.get("departureTime") != null) departureTime = LocalDateTime.parse(body.get("departureTime").toString());
            if (body.get("shorePowerKwh") != null) shorePowerKwh = Double.valueOf(body.get("shorePowerKwh").toString());
        }
        return dockingService.processDeparture(id, departureTime, shorePowerKwh);
    }
}
