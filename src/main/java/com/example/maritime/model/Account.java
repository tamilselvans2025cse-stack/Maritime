package com.example.maritime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String accountCode;

    @NotBlank
    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType; // ASSET, LIABILITY, INCOME, EXPENSE

    @ManyToOne
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount;

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
}
