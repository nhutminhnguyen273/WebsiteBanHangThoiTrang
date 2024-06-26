package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
