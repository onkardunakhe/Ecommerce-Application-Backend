package com.Crud.Crud.Tools;

import com.Crud.Crud.Dtos.ProductDTO;
import com.Crud.Crud.Entity.Product;
import com.Crud.Crud.Repository.Prodrepo;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProductTools {

    private final Prodrepo prodrepo;
    private final ModelMapper mapper;

    public ProductTools(Prodrepo prodrepo, ModelMapper mapper) {
        this.prodrepo = prodrepo;
        this.mapper = mapper;
    }

    @Tool(description = "Get the Current Date from this only")
    public String getCurrentDate() {
        System.out.println("date tool call");
        return LocalDateTime.now().toString();
    }

    //
//    @Tool(description = "Search products by name")
//    public List<Product> getProductsByName(@ToolParam(description = "This Is The Name You Want to Search in DB") String name) {
//        System.out.println("Tool called: search by name");
//        return prodrepo.findByNameContainingIgnoreCase(name);
//    }
    @Tool(description = "Search products by name")
    public List<ProductDTO> getProductsByName(String name) {
        System.out.println("Tool called: search by name");
        return prodrepo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(p -> new ProductDTO(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()))
                .toList();
    }

    @Tool(description = "Get products cheaper than a given price")
    public List<ProductDTO> getProductsUnderPrice(
            @ToolParam(description = "maximum price") double price) {

        System.out.println("Tool called: search by price");

        return prodrepo.findByPriceLessThanEqual(price)
                .stream()
                .map(p -> new ProductDTO(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()))
                .toList();
    }

    @Tool(description = "Find products by category like electronics, clothing, laptop, phone etc")
    public List<ProductDTO> getProductsByCategory(
            @ToolParam(description = "category of product") String category) {

        System.out.println("Tool called: search by category");

        return prodrepo.findByCategoryContainingIgnoreCase(category)
                .stream()
                .map(p -> new ProductDTO(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()))
                .toList();
    }

    @Tool(description = "Get cheapest products available")
    public List<ProductDTO> getCheapestProducts() {

        System.out.println("Tool called: cheapest products");

        return prodrepo.findTop5ByOrderByPriceAsc()
                .stream()
                .map(p -> new ProductDTO(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()))
                .toList();
    }

    @Tool(description = "Get most Premium/expensive products")
    public List<ProductDTO> getExpensiveProducts() {

        System.out.println("Tool called: expensive products");

        return prodrepo.findTop5ByOrderByPriceDesc()
                .stream()
                .map(p -> new ProductDTO(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()))
                .toList();
    }

    @Tool(description = "Get total number of products in the store")
    public long getTotalProducts() {

        System.out.println("Tool called: count products");

        return prodrepo.count();
    }

}
