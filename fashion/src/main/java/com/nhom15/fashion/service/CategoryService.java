package com.nhom15.fashion.service;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.repositories.CategoryRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll(){return categoryRepository.findAll();}

    public Optional<Category> getById(Long id){return categoryRepository.findById(id);}

    public void add(Category category){
        categoryRepository.save(category);
    }

    public void update(@NotNull Category category){
        var existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy loại sản phẩm này."));
        existingCategory.setName(category.getName());
        existingCategory.setHide(category.isHide());
        categoryRepository.save(existingCategory);
    }

    public void deleteById(Long id){
        if(!categoryRepository.existsById(id))
            throw new IllegalStateException("Không tìm thấy loại sản phẩm này.");
        categoryRepository.deleteById(id);
    }

}
