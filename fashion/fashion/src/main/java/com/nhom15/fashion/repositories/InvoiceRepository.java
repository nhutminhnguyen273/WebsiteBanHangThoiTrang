package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserId(Long userId);
    List<Invoice> findByCreatedDate(LocalDate date);
}
