package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.ProductDetails;
import com.nhom15.fashion.repositories.ProductRepository;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class AdminProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductRepository productRepository;
    private static String UPLOAD_DIR = "src/main/resources/static/image/";
    @GetMapping
    public String productList(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        List<Product> productList = productService.getAll();
        model.addAttribute("productList", productList);
        return "products/product-list";
    }

    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAll());
        return "products/add-product";
    }
    @PostMapping("/add")
    public String add(@RequestParam("imageFile") MultipartFile imageFile,
                      @RequestParam("detailsImages") MultipartFile[] detailsImages,
                      Product product, Model model) {
        try {
            if (!imageFile.isEmpty()) {
                // Lưu tệp vào thư mục
                String fileName = imageFile.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());

                // Lưu tên tệp vào thuộc tính avatar của sản phẩm
                product.setAvatar(fileName);
            }

            List<ProductDetails> detailsList = new ArrayList<>();
            for (MultipartFile detailsImage : detailsImages) {
                if (!detailsImage.isEmpty()) {
                    // Lưu tệp vào thư mục
                    String detailFileName = detailsImage.getOriginalFilename();
                    Path detailPath = Paths.get(UPLOAD_DIR + detailFileName);
                    Files.createDirectories(detailPath.getParent());
                    Files.write(detailPath, detailsImage.getBytes());

                    // Tạo đối tượng ProductDetails và thiết lập các thuộc tính
                    ProductDetails productDetails = new ProductDetails();
                    productDetails.setImage(detailFileName);
                    productDetails.setProduct(product);
                    detailsList.add(productDetails);
                }
            }

            product.setCreatedDate(LocalDate.now());
            product.setDetails(detailsList);
            productService.add(product);
            model.addAttribute("message", "Product added successfully");
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Failed to upload image");
        }
        return "redirect:/products";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAll());
        return "products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAll());
            return "products/update-product";
        }

        try {
            if (!imageFile.isEmpty()) {
                // Tạo tên tệp duy nhất
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                // Lưu tệp vào thư mục
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());

                // Lưu tên tệp vào thuộc tính avatar của sản phẩm
                product.setAvatar(fileName);
            }
            product.setCreatedDate(LocalDate.now());
            productRepository.save(product);
            model.addAttribute("message", "Product added successfully");
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Failed to upload image");
        }
        productService.update(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id){
        productService.deleteById(id);
        return "redirect:/products";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model){
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm này"));
        model.addAttribute("product", product);
        return "products/admin-product-details";
    }
}
