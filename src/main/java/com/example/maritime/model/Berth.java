package com.example.maritime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "berths")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Berth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String berthNumber;

    private String berthName;

    private String sector;

    @Positive
    private Double maxVesselLength;

    @Positive
    private Double maxDraft;

    @Positive
    private BigDecimal baseDockingRatePerHour;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Builder.Default
    private BerthStatus status = BerthStatus.AVAILABLE;
}
