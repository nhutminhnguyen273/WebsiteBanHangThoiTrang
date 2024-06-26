package com.nhom15.fashion.repositories;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    List<Product> findTop6ByCategoryOrderByPriceDesc(Category category);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :name")
    List<Product> findByCategoryId(@Param("name") String name);


}
