package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{name}")
    public String getProducts(Model model,
                              @PathVariable("name") String categoryName,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "4") int size) {
        int offset = page * size;

        List<Product> products = productService.getProductsByCategoryName(categoryName);

        int toIndex = Math.min(offset + size, products.size());
        List<Product> productsOnPage = products.subList(offset, toIndex);

        // Get total number of pages
        int totalPages = (int) Math.ceil((double) products.size() / size);

        model.addAttribute("products", productsOnPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);

        return "products/user-product-list";
    }

    @GetMapping("/shop")
    public String getAllProduct(Model model,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "4") int size) {
        int offset = page * size;

        List<Product> products = productService.getAll();

        int toIndex = Math.min(offset + size, products.size());
        List<Product> productsOnPage = products.subList(offset, toIndex);

        int totalPages = (int) Math.ceil((double) products.size() / size);

        model.addAttribute("products", productsOnPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);

        return "products/shop";
    }

    @GetMapping("/details/{id}")
    public String productDetails(@PathVariable Long id, Model model){
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm này"));

        DecimalFormat decimalFormat = new DecimalFormat("#,### VND");
        String formattedPrice = decimalFormat.format(product.getPrice());

        model.addAttribute("product", product);
        model.addAttribute("formattedPrice", formattedPrice);
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);
        return "products/user-product-details";
    }
    @GetMapping("/search")
    public String searchProducts(Model model,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "4") int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "redirect:/product/shop";
        }

        int offset = page * size;

        List<Product> products = productService.searchProducts(keyword);

        int toIndex = Math.min(offset + size, products.size());
        List<Product> productsOnPage = products.subList(offset, toIndex);

        int totalPages = (int) Math.ceil((double) products.size() / size);

        model.addAttribute("products", productsOnPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);

        return "products/search-results";
    }
}