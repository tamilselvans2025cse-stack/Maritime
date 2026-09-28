package com.example.maritime.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vendor_bills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String billNumber;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Contact vendor;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    private LocalDate billDate;
    private LocalDate dueDate;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BillStatus status = BillStatus.OPEN;
}
