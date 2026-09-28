package com.example.maritime.controller;

import com.example.maritime.dto.DockingRequest;
import com.example.maritime.model.DockingEvent;
import com.example.maritime.service.BerthService;
import com.example.maritime.service.DockingService;
import com.example.maritime.service.VesselService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DockingController {

    private final DockingService dockingService;
    private final VesselService vesselService;
    private final BerthService berthService;

    @GetMapping("/dockings")
    public String dockings(Model model) {
        model.addAttribute("dockings", dockingService.findAll());
        model.addAttribute("vessels", vesselService.findAll());
        model.addAttribute("berths", berthService.findAll());
        return "dockings";
    }

    @PostMapping("/dockings")
    public String createDocking(@RequestParam Long vesselId, @RequestParam Long berthId) {
        DockingRequest req = new DockingRequest();
        req.setVesselId(vesselId);
        req.setBerthId(berthId);
        dockingService.createDocking(req);
        return "redirect:/dockings";
    }

    @PostMapping("/dockings/{id}/departure")
    public String departFromUi(@PathVariable Long id, @RequestParam(required = false) Double shorePowerKwh) {
        dockingService.processDeparture(id, LocalDateTime.now(), shorePowerKwh);
        return "redirect:/dockings";
    }
}
