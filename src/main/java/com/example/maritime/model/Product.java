package com.example.maritime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String productCode;

    @NotBlank
    private String productName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductType productType; // SERVICE or GOODS

    @Positive
    private BigDecimal unitPrice;

    private String description;
}
