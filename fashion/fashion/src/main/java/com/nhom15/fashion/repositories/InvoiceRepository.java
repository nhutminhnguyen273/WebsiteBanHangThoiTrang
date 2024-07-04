package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserId(Long userId);
    List<Invoice> findByCreatedDate(LocalDate date);
    @Query("SELECT p.category.name, SUM(od.quantity), SUM(od.quantity * p.price) " +
            "FROM Invoice i JOIN i.order o JOIN o.orderDetails od JOIN od.product p " +
            "GROUP BY p.category.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findTopSellingCategories();
}
