package com.example.maritime.controller;

import com.example.maritime.model.Vessel;
import com.example.maritime.service.VesselService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Thymeleaf page controller for the Vessels screen. REST APIs live in VesselRestController. */
@Controller
@RequiredArgsConstructor
public class VesselController {

    private final VesselService vesselService;

    @GetMapping("/vessels")
    public String vessels(Model model) {
        model.addAttribute("vessels", vesselService.findAll());
        model.addAttribute("newVessel", new Vessel());
        return "vessels";
    }

    @PostMapping("/vessels")
    public String createVessel(@RequestParam String vesselName, @RequestParam String imoNumber,
                                @RequestParam(required = false) String shippingLine,
                                @RequestParam(required = false) String vesselType,
                                @RequestParam(required = false) String cargoType,
                                @RequestParam(required = false) Double cargoWeight,
                                @RequestParam(required = false) Double length,
                                @RequestParam(required = false) Double width,
                                @RequestParam(required = false) Double draft) {
        Vessel vessel = Vessel.builder()
                .vesselName(vesselName)
                .imoNumber(imoNumber)
                .shippingLine(shippingLine)
                .vesselType(vesselType)
                .cargoType(cargoType)
                .cargoWeight(cargoWeight)
                .length(length)
                .width(width)
                .draft(draft)
                .build();
        vesselService.save(vessel);
        return "redirect:/vessels";
    }
}
