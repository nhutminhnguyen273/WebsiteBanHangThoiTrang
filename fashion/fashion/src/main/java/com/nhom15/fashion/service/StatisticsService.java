package com.nhom15.fashion.service;

import com.nhom15.fashion.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {
    @Autowired
    private InvoiceRepository invoiceRepository;


    public List<Map<String, Object>> getTopSellingCategories() {
        List<Object[]> results = invoiceRepository.findTopSellingCategories();
        List<Map<String, Object>> categorySales = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> categorySale = new HashMap<>();
            categorySale.put("categoryName", result[0]);
            categorySale.put("quantitySold", result[1]);
            categorySale.put("totalRevenue", result[2]);
            categorySales.add(categorySale);
        }
        return categorySales;
    }
}
