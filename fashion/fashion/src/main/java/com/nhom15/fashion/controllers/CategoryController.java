package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    @GetMapping
    public String categoriesList(Model model){
        List<Category> categoryList = categoryService.getAll();
        model.addAttribute("categories", categoryList);
        return "categories/categories-list";
    }

    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("category", new Category());
        return "categories/add-category";
    }
    @PostMapping("/add")
    public String addCategory(@Valid Category category, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "categories/add-category";
        }
        categoryService.add(category);
        return "redirect:/categories";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model){
        Category category = categoryService.getById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại sản phẩm này."));
        model.addAttribute("category", category);
        return "categories/update-category";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid Category category, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            category.setId(id);
            model.addAttribute("category", category);
            return "categories/update-category";
        }
        categoryService.update(category);
        model.addAttribute("categories", categoryService.getAll());
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model){
        Category category = categoryService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm này."));
        categoryService.deleteById(id);
        model.addAttribute("categories", categoryService.getAll());
        return "redirect:/categories";
    }
}
