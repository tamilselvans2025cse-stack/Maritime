package com.example.maritime.controller;

import com.example.maritime.dto.TelemetryRequest;
import com.example.maritime.model.Vessel;
import com.example.maritime.service.VesselService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class VesselRestController {

    private final VesselService vesselService;

    @GetMapping("/vessels")
    public List<Vessel> getAll() {
        return vesselService.findAll();
    }

    @GetMapping("/vessels/{id}")
    public Vessel getOne(@PathVariable Long id) {
        return vesselService.findById(id);
    }

    @PostMapping("/vessels")
    public Vessel create(@Valid @RequestBody Vessel vessel) {
        return vesselService.save(vessel);
    }

    @PutMapping("/vessels/{id}")
    public Vessel update(@PathVariable Long id, @Valid @RequestBody Vessel vessel) {
        return vesselService.update(id, vessel);
    }

    @DeleteMapping("/vessels/{id}")
    public void delete(@PathVariable Long id) {
        vesselService.delete(id);
    }

    @PostMapping("/telemetry")
    public Vessel recordTelemetry(@Valid @RequestBody TelemetryRequest request) {
        return vesselService.recordTelemetry(request);
    }

    @GetMapping("/telemetry/{vesselId}")
    public Vessel getTelemetry(@PathVariable Long vesselId) {
        return vesselService.findById(vesselId);
    }
}
