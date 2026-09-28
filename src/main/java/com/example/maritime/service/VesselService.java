package com.example.maritime.service;

import com.example.maritime.dto.TelemetryRequest;
import com.example.maritime.exception.ResourceNotFoundException;
import com.example.maritime.model.Vessel;
import com.example.maritime.repository.VesselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VesselService {

    private final VesselRepository vesselRepository;

    public List<Vessel> findAll() {
        return vesselRepository.findAll();
    }

    public Vessel findById(Long id) {
        return vesselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vessel not found with id " + id));
    }

    public Vessel save(Vessel vessel) {
        return vesselRepository.save(vessel);
    }

    public Vessel update(Long id, Vessel updated) {
        Vessel existing = findById(id);
        existing.setVesselName(updated.getVesselName());
        existing.setImoNumber(updated.getImoNumber());
        existing.setShippingLine(updated.getShippingLine());
        existing.setVesselType(updated.getVesselType());
        existing.setCargoType(updated.getCargoType());
        existing.setCargoWeight(updated.getCargoWeight());
        existing.setLength(updated.getLength());
        existing.setWidth(updated.getWidth());
        existing.setDraft(updated.getDraft());
        existing.setStatus(updated.getStatus());
        return vesselRepository.save(existing);
    }

    public void delete(Long id) {
        vesselRepository.delete(findById(id));
    }

    /**
     * Simulated AIS/LiDAR telemetry ingestion. This does NOT control a real
     * vessel - it simply records the latest reported position/sensor values
     * so the dashboard/navigation page can visualize them.
     */
    public Vessel recordTelemetry(TelemetryRequest request) {
        Vessel vessel = findById(request.getVesselId());
        vessel.setLatitude(request.getLatitude());
        vessel.setLongitude(request.getLongitude());
        vessel.setSpeed(request.getSpeed());
        vessel.setHeading(request.getHeading());
        vessel.setAisSignalStrength(request.getAisSignalStrength());
        vessel.setLidarObstacleDistance(request.getLidarObstacleDistance());
        return vesselRepository.save(vessel);
    }
}
