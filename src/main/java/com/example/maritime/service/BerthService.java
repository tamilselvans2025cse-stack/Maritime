package com.example.maritime.service;

import com.example.maritime.exception.ResourceNotFoundException;
import com.example.maritime.model.Berth;
import com.example.maritime.repository.BerthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BerthService {

    private final BerthRepository berthRepository;

    public List<Berth> findAll() {
        return berthRepository.findAll();
    }

    public Berth findById(Long id) {
        return berthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Berth not found with id " + id));
    }

    public Berth save(Berth berth) {
        return berthRepository.save(berth);
    }

    public Berth update(Long id, Berth updated) {
        Berth existing = findById(id);
        existing.setBerthNumber(updated.getBerthNumber());
        existing.setBerthName(updated.getBerthName());
        existing.setSector(updated.getSector());
        existing.setMaxVesselLength(updated.getMaxVesselLength());
        existing.setMaxDraft(updated.getMaxDraft());
        existing.setBaseDockingRatePerHour(updated.getBaseDockingRatePerHour());
        existing.setStatus(updated.getStatus());
        return berthRepository.save(existing);
    }

    public void delete(Long id) {
        berthRepository.delete(findById(id));
    }
}
