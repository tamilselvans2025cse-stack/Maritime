package com.example.maritime.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DockingRequest {

    @NotNull
    private Long vesselId;

    @NotNull
    private Long berthId;

    private LocalDateTime arrivalTime;
    private LocalDateTime dockingStartTime;
    private LocalDateTime departureTime;

    private Double shorePowerKwh;
}
