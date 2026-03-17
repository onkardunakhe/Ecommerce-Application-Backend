package com.Crud.Crud.Tools;

import com.Crud.Crud.Dtos.ProductDTO;
import com.Crud.Crud.Entity.Product;
import com.Crud.Crud.Repository.Prodrepo;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ProductTools {

    private final Prodrepo prodrepo;
    private final ModelMapper mapper;

    public ProductTools(Prodrepo prodrepo, ModelMapper mapper) {
        this.prodrepo = prodrepo;
        this.mapper = mapper;
    }

    @Tool(description = "Extract filters like price and category from user query")
    public Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();

        query = query.toLowerCase();

        // Detect category
        if (query.contains("phone")) result.put("category", "phone");
        if (query.contains("laptop")) result.put("category", "laptop");
        if (query.contains("clothing")) result.put("category", "clothing");

        // Detect price
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            result.put("price", matcher.group(1));
        }

        return result;
    }

    @Tool(description = "Smart product search using filters like price and category")
    public List<ProductDTO> smartSearch(String query) {

        Map<String, String> filters = parseQuery(query);

        String category = filters.get("category");
        String priceStr = filters.get("price");

        List<Product> products;

        if (category != null && priceStr != null) {
            double price = Double.parseDouble(priceStr);
            products = prodrepo
                    .findByCategoryContainingIgnoreCaseAndPriceLessThanEqual(category, price);
        } else if (category != null) {
            products = prodrepo.findByCategoryContainingIgnoreCase(category);
        } else if (priceStr != null) {
            double price = Double.parseDouble(priceStr);
            products = prodrepo.findByPriceLessThanEqual(price);
        } else {
            products = prodrepo.findAll();
        }

        return products.stream()
                .map(p -> new ProductDTO(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()))
                .toList();
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
    public List<ProductDTO> getProductsByName(@ToolParam(description = "this is name of product") String name) {
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

    @Tool(description = "Compare two products and return differences")
    public String compareProducts(String name1, String name2) {

        Product p1 = prodrepo.findFirstByNameContainingIgnoreCase(name1);
        Product p2 = prodrepo.findFirstByNameContainingIgnoreCase(name2);

        if (p1 == null || p2 == null) {
            return "One or both products not found";
        }

        return """
                Comparison:
                
                %s:
                Price: %.2f
                Category: %s
                
                %s:
                Price: %.2f
                Category: %s
                
                %s is %s than %s
                """.formatted(
                p1.getName(), p1.getPrice(), p1.getCategory(),
                p2.getName(), p2.getPrice(), p2.getCategory(),
                p1.getPrice() > p2.getPrice() ? p1.getName() : p2.getName(),
                p1.getPrice() > p2.getPrice() ? "more expensive" : "cheaper",
                p1.getPrice() > p2.getPrice() ? p2.getName() : p1.getName()
        );
    }

    @Tool(description = "Recommend similar products based on name")
    public List<ProductDTO> recommendProducts(String name) {

        Product base = prodrepo.findFirstByNameContainingIgnoreCase(name);

        if (base == null) return List.of();

        return prodrepo.findByCategoryContainingIgnoreCase(base.getCategory())
                .stream()
                .filter(p -> !p.getName().equalsIgnoreCase(base.getName()))
                .limit(5)
                .map(p -> new ProductDTO(
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()))
                .toList();
    }

    @Tool(description = "Get average price per category")
    public Map<String, Double> getCategoryInsights() {

        List<Product> products = prodrepo.findAll();

        return products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(Product::getPrice)
                ));
    }
}
