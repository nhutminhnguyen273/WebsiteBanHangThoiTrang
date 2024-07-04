package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String home(Model model){
        // Fetch all categories
        List<Category> categories = categoryService.getAll();

        // Optional: Apply any filter to the categories if needed
        List<Category> filteredCategories = categories.stream()
                .filter(category ->  category.isHide() == false)
                .collect(Collectors.toList());

        // Fetch top 6 products for each filtered category
        Map<Category, List<Product>> categoryProductsMap = filteredCategories.stream()
                .collect(Collectors.toMap(
                        category -> category,
                        category -> productService.findTop6ByCategoryOrderByPriceDesc(category)
                ));

        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categoriesList", categoriesList);
        // Add the filtered categories and products to the model
        model.addAttribute("categories", filteredCategories);
        model.addAttribute("categoryProductsMap", categoryProductsMap);
        return "home/home";
    }
}
