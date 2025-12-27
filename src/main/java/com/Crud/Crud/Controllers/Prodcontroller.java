package com.Crud.Crud.Controllers;

import com.Crud.Crud.Repository.Prodrepo;
import com.Crud.Crud.Service.Prodservice;
import com.Crud.Crud.Entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Prodcontroller {
    @Autowired
    private final Prodservice ps;
    @Autowired
    Prodrepo prodrepo;


    public Prodcontroller(Prodservice ps) {
        this.ps = ps;
    }


    @GetMapping("/findbyid/{id}")
    public ResponseEntity<?> FindProdbyId(@PathVariable Long id) {
        return ps.FindByID(id);
    }


    @GetMapping("/getallproducts")
    public ResponseEntity<List<Map<String, Object>>> Getallproducts() {
        return ps.getAllProds();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("createproduct")
    public ResponseEntity<Product> CreateProduct(@RequestBody Product product) {
        return ps.CreateProduct(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateproduct/{id}")
    public ResponseEntity<Product> UpdateProduct(@RequestBody Product product, @PathVariable Long id) {
        return ps.UpdateProduct(product, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteprod/{id}")
    public String DeleteProduct(@PathVariable Long id) {
        return ps.DeleteProduct(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createprodimg")
    public ResponseEntity<?> addProductWithImg(
            @RequestPart("product") String product,
            @RequestPart("imgfile") MultipartFile imgfile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Product product1 = mapper.readValue(product, Product.class);

            Product savedProd = ps.saveProdWithImg(product1, imgfile);
            return new ResponseEntity<>(savedProd, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateprodimg/{id}")
    public ResponseEntity<?> updateProductWithImg(
            @PathVariable Long id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "imgfile", required = false) MultipartFile imgfile) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Product product = mapper.readValue(productJson, Product.class);

            Product updated = ps.updateProductWithImg(id, product, imgfile);
            return new ResponseEntity<>(updated, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/product/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        return ps.getProductImage(id);
    }


    @GetMapping("/findbyname/{name}")
    public ResponseEntity<?> findprodname(@PathVariable String name) {
        return ps.FindByName(name);
    }

    @GetMapping("/search/category")
    public ResponseEntity<?> SearchBycategory(@RequestParam String category) {
        ResponseEntity<?> products = ps.SearchBycategory(category);
        return products;
    }

    @GetMapping("/productwithimg/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return ps.GetProductWithImg(id);
    }

    @GetMapping("/getprodbetween")
    public ResponseEntity<?> getProdBetween(@RequestParam Long minprice, @RequestParam Long maxprice) {
        return ps.FindProdPriceBetween(minprice, maxprice);
    }

}
