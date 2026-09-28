package com.example.maritime.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class InvoiceRequest {

    private Long dockingEventId;
    private Long customerId;
}
