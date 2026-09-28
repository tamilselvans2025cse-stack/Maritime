package com.example.maritime.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentReference;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType; // BANK or CASH

    private BigDecimal amount;

    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Contact customer;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Contact vendor;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private CustomerInvoice invoice;

    @ManyToOne
    @JoinColumn(name = "vendor_bill_id")
    private VendorBill vendorBill;

    private String status; // e.g. COMPLETED
}
