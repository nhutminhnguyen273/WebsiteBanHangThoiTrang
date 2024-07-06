package com.nhom15.fashion.service;
import com.nhom15.fashion.models.Invoice;
import com.nhom15.fashion.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public Map<LocalDate, Long> getRevenueStatistics() {
        List<Invoice> invoices = invoiceRepository.findAll();

        return invoices.stream()
                .collect(Collectors.groupingBy(
                        Invoice::getCreatedDate,
                        Collectors.summingLong(Invoice::getTotalAmount)
                ));
    }
}