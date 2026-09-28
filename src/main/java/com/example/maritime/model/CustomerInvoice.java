package com.example.maritime.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer_invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Contact customer;

    @ManyToOne
    @JoinColumn(name = "vessel_id")
    private Vessel vessel;

    @ManyToOne
    @JoinColumn(name = "docking_event_id")
    private DockingEvent dockingEvent;

    private LocalDate invoiceDate;
    private LocalDate dueDate;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;
}
