package com.nhom15.fashion.service;

import com.nhom15.fashion.models.*;
import com.nhom15.fashion.repositories.ProductDetailsRepository;
import com.nhom15.fashion.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductDetailsRepository productDetailsRepository;

    public List<Product> getAll(){return productRepository.findAll();}

    public Optional<Product> getById(Long id){return productRepository.findById(id);}

    public void add(Product product){
        product.setCreatedDate(LocalDate.now());
        productRepository.save(product);
        for (ProductDetails detail : product.getDetails()) {
            detail.setProduct(product);
            productDetailsRepository.save(detail);
        }
    }

    public void update(Product product){
        var existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm này."));
        existingProduct.setName(product.getName());
        existingProduct.setCreatedDate(product.getCreatedDate());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setAvatar(product.getAvatar());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setHide(product.isHide());
        existingProduct.setCategory(product.getCategory());
        productRepository.save(existingProduct);
    }

    public void deleteById(Long id){
        if(!productRepository.existsById(id))
            throw new IllegalStateException("Không tìm thấy sản phẩm này.");
        productRepository.deleteById(id);
    }

    public List<Product> findTop6ByCategoryOrderByPriceDesc(Category category) {
        return productRepository.findTop6ByCategoryOrderByPriceDesc(category);
    }

    public List<Product> getProductsByCategoryName(String name) {
        return productRepository.findByCategoryId(name);
    }
    @Transactional
    public void updateProductQuantities(List<OrderDetails> orderDetailsList) {
        for (OrderDetails orderDetails : orderDetailsList) {
            Long productId = orderDetails.getProduct().getId();
            int orderedQuantity = orderDetails.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm có id: " + productId));

            int currentQuantity = product.getQuantity();
            int updatedQuantity = currentQuantity - orderedQuantity;

            if (updatedQuantity < 0) {
                throw new IllegalStateException("Số lượng sản phẩm không đủ trong kho");
            }

            product.setQuantity(updatedQuantity);
            productRepository.save(product);
        }
    }
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword);
    }
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }
}
