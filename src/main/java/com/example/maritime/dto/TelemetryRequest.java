package com.example.maritime.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TelemetryRequest {

    @NotNull
    private Long vesselId;

    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
    private Double aisSignalStrength;
    private Double lidarObstacleDistance;
}
