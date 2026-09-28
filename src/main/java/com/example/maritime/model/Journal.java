package com.example.maritime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "journals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String journalCode; // SALES, PURCHASE, BANK, CASH

    private String journalName;

    @Enumerated(EnumType.STRING)
    private JournalType journalType;
}
