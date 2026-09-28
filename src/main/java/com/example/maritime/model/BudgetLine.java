package com.example.maritime.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "budget_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BudgetLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private BigDecimal plannedAmount;

    @Builder.Default
    private BigDecimal actualAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal variance = BigDecimal.ZERO;
}
