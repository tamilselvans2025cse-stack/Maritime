package com.example.maritime.service;

import com.example.maritime.dto.DockingRequest;
import com.example.maritime.exception.ResourceNotFoundException;
import com.example.maritime.model.*;
import com.example.maritime.repository.DockingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orchestrates the vessel arrival -> berth reservation -> docking ->
 * departure workflow described in the project spec (section 16/41).
 */
@Service
@RequiredArgsConstructor
public class DockingService {

    private final DockingEventRepository dockingEventRepository;
    private final VesselService vesselService;
    private final BerthService berthService;
    private final BillingService billingService;

    public List<DockingEvent> findAll() {
        return dockingEventRepository.findAll();
    }

    public DockingEvent findById(Long id) {
        return dockingEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docking event not found with id " + id));
    }

    @Transactional
    public DockingEvent createDocking(DockingRequest request) {
        Vessel vessel = vesselService.findById(request.getVesselId());
        Berth berth = berthService.findById(request.getBerthId());

        if (berth.getStatus() != BerthStatus.AVAILABLE && berth.getStatus() != BerthStatus.RESERVED) {
            throw new IllegalStateException("Berth " + berth.getBerthNumber() + " is not available");
        }
        if (vessel.getLength() != null && berth.getMaxVesselLength() != null && vessel.getLength() > berth.getMaxVesselLength()) {
            throw new IllegalStateException("Vessel is too large for this berth");
        }
        if (vessel.getDraft() != null && berth.getMaxDraft() != null && vessel.getDraft() > berth.getMaxDraft()) {
            throw new IllegalStateException("Vessel draft is too deep for this berth");
        }

        berth.setStatus(BerthStatus.OCCUPIED);
        berthService.save(berth);

        vessel.setStatus(VesselStatus.DOCKED);
        vesselService.save(vessel);

        DockingEvent event = DockingEvent.builder()
                .vessel(vessel)
                .berth(berth)
                .arrivalTime(request.getArrivalTime() != null ? request.getArrivalTime() : LocalDateTime.now())
                .dockingStartTime(request.getDockingStartTime() != null ? request.getDockingStartTime() : LocalDateTime.now())
                .status(DockingStatus.DOCKED)
                .build();

        return dockingEventRepository.save(event);
    }

    @Transactional
    public DockingEvent processDeparture(Long dockingEventId, LocalDateTime departureTime, Double shorePowerKwh) {
        DockingEvent event = findById(dockingEventId);
        if (event.getStatus() == DockingStatus.COMPLETED) {
            throw new IllegalStateException("Docking event " + dockingEventId + " is already completed");
        }

        LocalDateTime actualDeparture = departureTime != null ? departureTime : LocalDateTime.now();
        event.setDepartureTime(actualDeparture);

        double occupancyHours = Duration.between(event.getDockingStartTime(), actualDeparture).toMinutes() / 60.0;
        if (occupancyHours < 0) occupancyHours = 0;
        event.setOccupancyDurationHours(occupancyHours);

        double kwh = shorePowerKwh != null ? shorePowerKwh : 0.0;
        event.setShorePowerKwh(kwh);

        Berth berth = event.getBerth();
        var dockingFee = billingService.calculateDockingFee(occupancyHours, berth.getBaseDockingRatePerHour());
        var shorePowerFee = billingService.calculateShorePowerFee(kwh);

        event.setDockingFee(dockingFee);
        event.setShorePowerFee(shorePowerFee);
        event.setTotalFee(dockingFee.add(shorePowerFee));
        event.setStatus(DockingStatus.COMPLETED);

        berth.setStatus(BerthStatus.AVAILABLE);
        berthService.save(berth);

        Vessel vessel = event.getVessel();
        vessel.setStatus(VesselStatus.DEPARTED);
        vesselService.save(vessel);

        return dockingEventRepository.save(event);
    }
}
