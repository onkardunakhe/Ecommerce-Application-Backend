package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Prodrepo extends JpaRepository<Product, Long> {
    public List<Product> findByName(String name);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    public List<Product> findByCategoryContainingIgnoreCase(String category);

    List<Product> findByPriceBetween(Long minprice, Long maxPrice);

}
