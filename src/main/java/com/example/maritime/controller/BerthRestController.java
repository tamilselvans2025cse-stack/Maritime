package com.example.maritime.controller;

import com.example.maritime.model.Berth;
import com.example.maritime.service.BerthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class BerthRestController {

    private final BerthService berthService;

    @GetMapping("/berths")
    public List<Berth> getAll() {
        return berthService.findAll();
    }

    @PostMapping("/berths")
    public Berth create(@Valid @RequestBody Berth berth) {
        return berthService.save(berth);
    }

    @PutMapping("/berths/{id}")
    public Berth update(@PathVariable Long id, @Valid @RequestBody Berth berth) {
        return berthService.update(id, berth);
    }

    @DeleteMapping("/berths/{id}")
    public void delete(@PathVariable Long id) {
        berthService.delete(id);
    }
}
