package com.example.maritime;

import com.example.maritime.dto.DockingRequest;
import com.example.maritime.dto.PaymentRequest;
import com.example.maritime.model.*;
import com.example.maritime.repository.*;
import com.example.maritime.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Loads sample/demo data on startup (section 34 of the spec) so the app is
 * immediately explorable without manual data entry. Runs only if the
 * database is empty.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final VesselRepository vesselRepository;
    private final BerthRepository berthRepository;
    private final DockingEventRepository dockingEventRepository;
    private final VesselService vesselService;
    private final BerthService berthService;
    private final DockingService dockingService;

    @Override
    public void run(String... args) {
        if (vesselRepository.count() > 0 || berthRepository.count() > 0) {
            return;
        }

        Berth berth1 = berthService.save(Berth.builder()
                .berthNumber("Berth 01")
                .berthName("Main Dock")
                .sector("North")
                .maxVesselLength(250.0)
                .maxDraft(12.0)
                .baseDockingRatePerHour(new BigDecimal("5000"))
                .status(BerthStatus.AVAILABLE)
                .build());

        Berth berth2 = berthService.save(Berth.builder()
                .berthNumber("Berth 02")
                .berthName("Secondary Dock")
                .sector("South")
                .maxVesselLength(180.0)
                .maxDraft(9.0)
                .baseDockingRatePerHour(new BigDecimal("3500"))
                .status(BerthStatus.AVAILABLE)
                .build());

        Vessel vessel1 = vesselService.save(Vessel.builder()
                .vesselName("Ocean Mercy")
                .imoNumber("IMO90001")
                .cargoType("Containers")
                .length(220.0)
                .draft(10.5)
                .speed(9.5)
                .status(VesselStatus.WAITING)
                .build());

        Vessel vessel2 = vesselService.save(Vessel.builder()
                .vesselName("Blue Horizon")
                .imoNumber("IMO90002")
                .cargoType("Bulk Cargo")
                .length(180.0)
                .draft(8.5)
                .speed(7.2)
                .status(VesselStatus.NAVIGATING)
                .build());

        DockingRequest request = new DockingRequest();
        request.setVesselId(vessel1.getId());
        request.setBerthId(berth1.getId());
        request.setArrivalTime(LocalDateTime.now().minusHours(6));
        request.setDockingStartTime(LocalDateTime.now().minusHours(5));

        DockingEvent event = dockingService.createDocking(request);
        dockingService.processDeparture(event.getId(), LocalDateTime.now().minusHours(1), 280.0);

        DockingRequest request2 = new DockingRequest();
        request2.setVesselId(vessel2.getId());
        request2.setBerthId(berth2.getId());
        request2.setArrivalTime(LocalDateTime.now().minusHours(2));
        request2.setDockingStartTime(LocalDateTime.now().minusHours(1));
        dockingService.createDocking(request2);
    }
}
