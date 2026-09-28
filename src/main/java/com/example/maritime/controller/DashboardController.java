package com.example.maritime.controller;

import com.example.maritime.model.*;
import com.example.maritime.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final VesselService vesselService;
    private final BerthService berthService;
    private final DockingService dockingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Vessel> vessels = vesselService.findAll();
        List<Berth> berths = berthService.findAll();
        List<DockingEvent> dockings = dockingService.findAll();

        long waitingVessels = vessels.stream().filter(v -> v.getStatus() == VesselStatus.WAITING).count();
        long dockedVessels = vessels.stream().filter(v -> v.getStatus() == VesselStatus.DOCKED).count();
        long departedVessels = vessels.stream().filter(v -> v.getStatus() == VesselStatus.DEPARTED).count();

        long availableBerths = berths.stream().filter(b -> b.getStatus() == BerthStatus.AVAILABLE).count();
        long occupiedBerths = berths.stream().filter(b -> b.getStatus() == BerthStatus.OCCUPIED).count();

        BigDecimal todaysRevenue = dockings.stream()
                .filter(d -> d.getDepartureTime() != null && d.getDepartureTime().toLocalDate().equals(LocalDate.now()))
                .map(DockingEvent::getTotalFee)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalVessels", vessels.size());
        model.addAttribute("waitingVessels", waitingVessels);
        model.addAttribute("dockedVessels", dockedVessels);
        model.addAttribute("departedVessels", departedVessels);
        model.addAttribute("availableBerths", availableBerths);
        model.addAttribute("occupiedBerths", occupiedBerths);
        model.addAttribute("todaysRevenue", todaysRevenue);
        model.addAttribute("vessels", vessels);
        model.addAttribute("berths", berths);
        model.addAttribute("dockings", dockings);
        return "dashboard";
    }

    @GetMapping("/navigation")
    public String navigation(Model model) {
        model.addAttribute("vessels", vesselService.findAll());
        return "navigation";
    }
}
