package com.example.maritime.controller;

import com.example.maritime.model.Berth;
import com.example.maritime.service.BerthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BerthController {

    private final BerthService berthService;

    @GetMapping("/berths")
    public String berths(Model model) {
        model.addAttribute("berths", berthService.findAll());
        return "berths";
    }

    @PostMapping("/berths")
    public String createBerth(@RequestParam String berthNumber, @RequestParam(required = false) String berthName,
                               @RequestParam(required = false) String sector,
                               @RequestParam(required = false) Double maxVesselLength,
                               @RequestParam(required = false) Double maxDraft,
                               @RequestParam(required = false) java.math.BigDecimal baseDockingRatePerHour) {
        Berth berth = Berth.builder()
                .berthNumber(berthNumber)
                .berthName(berthName)
                .sector(sector)
                .maxVesselLength(maxVesselLength)
                .maxDraft(maxDraft)
                .baseDockingRatePerHour(baseDockingRatePerHour)
                .build();
        berthService.save(berth);
        return "redirect:/berths";
    }
}
