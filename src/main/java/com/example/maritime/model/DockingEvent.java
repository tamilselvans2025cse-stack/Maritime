package com.example.maritime.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "docking_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DockingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vessel_id")
    private Vessel vessel;

    @ManyToOne
    @JoinColumn(name = "berth_id")
    private Berth berth;

    private LocalDateTime arrivalTime;
    private LocalDateTime dockingStartTime;
    private LocalDateTime departureTime;

    private Double occupancyDurationHours;
    private Double shorePowerKwh;

    private BigDecimal dockingFee;
    private BigDecimal shorePowerFee;
    private BigDecimal totalFee;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DockingStatus status = DockingStatus.ARRIVED;
}
