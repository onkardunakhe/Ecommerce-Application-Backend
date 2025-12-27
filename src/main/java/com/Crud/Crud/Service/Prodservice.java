package com.Crud.Crud.Service;

import com.Crud.Crud.Entity.Product;
import com.Crud.Crud.Repository.Prodrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.*;

@Service
public class Prodservice {
    @Autowired
    Prodrepo prodrepo;


    public ResponseEntity<Product> CreateProduct(Product product) {
        return new ResponseEntity<>(prodrepo.save(product), HttpStatus.CREATED);
    }


    public ResponseEntity<List<Map<String, Object>>> getAllProds() {
        List<Product> products = prodrepo.findAll();

        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Product p : products) {
            // Build full image URL for each product
            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/product/")
                    .path(String.valueOf(p.getId()))
                    .path("/image")
                    .toUriString();

            // Create a map to hold product data + image link
            Map<String, Object> data = new HashMap<>();
            data.put("id", p.getId());
            data.put("name", p.getName());
            data.put("category", p.getCategory());
            data.put("price", p.getPrice());
            data.put("imgname", p.getImgname());
            data.put("imgtype", p.getImgtype());
            data.put("imageUrl", imageUrl);

            responseList.add(data);
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }


    public ResponseEntity<Product> UpdateProduct(Product product, Long id) {
        Optional<Product> existingProduct = prodrepo.findById(id);

        if (existingProduct.isPresent()) {
            Product p = existingProduct.get();
            // ✅ Copy only the fields you want to update
            p.setName(product.getName());
            p.setCategory(product.getCategory());
            p.setPrice(product.getPrice());

            return new ResponseEntity<>(prodrepo.save(p), HttpStatus.ACCEPTED); // ✅ save updated entity
        } else {
            // not found → handle properly
            throw new RuntimeException("Product not found with id " + id);
        }
    }

    public String DeleteProduct(Long id) {
        Optional<Product> product = prodrepo.findById(id);
        if (product.isPresent()) {
            prodrepo.deleteById(id);
            return "Product deleted successfully with id " + id;
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }


    public Product saveProdWithImg(Product product, MultipartFile imgfile) throws IOException {
        product.setImgname(imgfile.getOriginalFilename());
        product.setImgtype(imgfile.getContentType());
        product.setImgbyt(imgfile.getBytes());
        return prodrepo.save(product);
    }

    public Product updateProductWithImg(Long id, Product newProduct, MultipartFile imgfile) throws IOException {
        Product existing = prodrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update normal fields
        existing.setName(newProduct.getName());
        existing.setCategory(newProduct.getCategory());
        existing.setPrice(newProduct.getPrice());

        // Update image only if new one provided
        if (imgfile != null && !imgfile.isEmpty()) {
            existing.setImgname(imgfile.getOriginalFilename());
            existing.setImgtype(imgfile.getContentType());
            existing.setImgbyt(imgfile.getBytes());
        }

        return prodrepo.save(existing);
    }


    public ResponseEntity<byte[]> getProductImage(Long id) {
        Product product = prodrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + product.getImgname() + "\"")
                .contentType(MediaType.parseMediaType(product.getImgtype()))
                .body(product.getImgbyt());
    }

    public ResponseEntity<?> GetProductWithImg(Long id) {
        Optional<Product> productOpt = prodrepo.findById(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Create image URL (points to /products/{id}/image)
            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/product/")
                    .path(String.valueOf(product.getId()))
                    .path("/image")
                    .toUriString();

            // Build JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("id", product.getId());
            response.put("name", product.getName());
            response.put("category", product.getCategory());
            response.put("price", product.getPrice());
            response.put("imgname", product.getImgname());
            response.put("imgtype", product.getImgtype());
            response.put("imageUrl", imageUrl); // 👈 add link instead of image bytes

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> FindByName(String name) {
        List<Product> products = prodrepo.findByNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }


    public ResponseEntity<?> FindByID(Long id) {
        Optional<Product> product = prodrepo.findById(id);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> SearchBycategory(String word) {
        List<Product> products = prodrepo.findByCategoryContainingIgnoreCase(word);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok((products));
        }
    }

    public ResponseEntity<?> FindProdPriceBetween(Long minp, Long maxp) {
        List<Product> products = prodrepo.findByPriceBetween(minp, maxp);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(products);
        }

    }


}
