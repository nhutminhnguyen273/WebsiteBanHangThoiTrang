package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

  /*  @RequestMapping("/{name}")
    public String getProductsByCategoryName(@PathVariable("name") String categoryName, Model model) {
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        model.addAttribute("products", products);
        return "products/user-product-list";
    }*/

    @GetMapping("/details/{id}")
    public String productDetails(@PathVariable Long id, Model model){
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm này"));
        model.addAttribute("product", product);
        return "products/user-product-details";
    }
    /*@GetMapping("/{name}")
    public String getProducts(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "2") int size) {
        Page<Product> productPage = productService.findPaginated(page, size);
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "products/user-product-list";
    }*/
    @GetMapping("/{name}")
    public String getProducts(Model model,
                              @PathVariable("name") String categoryName,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "4") int size) {
        // Calculate the offset based on page number and size
        int offset = page * size;

        // Get products by category name
        List<Product> products = productService.getProductsByCategoryName(categoryName);

        // Slice the list to get products for the current page
        int toIndex = Math.min(offset + size, products.size());
        List<Product> productsOnPage = products.subList(offset, toIndex);

        // Get total number of pages
        int totalPages = (int) Math.ceil((double) products.size() / size);

        model.addAttribute("products", productsOnPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "products/user-product-list";
    }



}