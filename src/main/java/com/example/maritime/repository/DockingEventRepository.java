package com.example.maritime.repository;

import com.example.maritime.model.DockingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockingEventRepository extends JpaRepository<DockingEvent, Long> {
}
