package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/{name}")
    public String getProductsByCategoryName(@PathVariable("name") String categoryName, Model model) {
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        model.addAttribute("products", products);
        return "products/user-product-list";
    }

    @GetMapping("/details/{id}")
    public String productDetails(@PathVariable Long id, Model model){
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm này"));
        model.addAttribute("product", product);
        return "products/user-product-details";
    }

}