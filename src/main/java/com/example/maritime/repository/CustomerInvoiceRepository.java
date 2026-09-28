package com.example.maritime.repository;

import com.example.maritime.model.CustomerInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {
}
