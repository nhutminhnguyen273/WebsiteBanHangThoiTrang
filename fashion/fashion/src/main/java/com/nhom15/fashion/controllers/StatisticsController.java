package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.OrderDetails;
import com.nhom15.fashion.repositories.OrderDetailRepository;
import com.nhom15.fashion.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private OrderDetailRepository orderDetailsRepository;

    @GetMapping("/statistics")
    public String getStatistics(Model model) {
        List<Map<String, Object>> categorySales = statisticsService.getTopSellingCategories();

        model.addAttribute("categorySales", categorySales);
        return "statistic/statistics";
    }
}
