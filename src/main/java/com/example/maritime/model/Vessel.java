package com.example.maritime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "vessels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vessel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String vesselName;

    @NotBlank
    private String imoNumber;

    private String cargoType;

    @Positive
    private Double length;

    @Positive
    private Double draft;

    @PositiveOrZero
    private Double speed;

    private Double latitude;
    private Double longitude;
    private Double heading;
    private Double aisSignalStrength;
    private Double lidarObstacleDistance;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Builder.Default
    private VesselStatus status = VesselStatus.WAITING;
}
