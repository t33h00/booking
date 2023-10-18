package com.lotus.booking.Controllers;

import com.lotus.booking.Entity.Products;
import com.lotus.booking.Repository.ProductRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(exposedHeaders = {"Access-Control-Allow-Origin","Access-Control-Allow-Credentials"}, maxAge = 3600)
public class ProductApi {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Products> listOfProducts(){
        return productRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Products> saveProduct(@RequestBody @Validated Products products){
        Products saveProducts =  productRepository.save(products);
        URI productURI = URI.create("/products/" + saveProducts.getId());
        return ResponseEntity.created(productURI).body(saveProducts);
    }
}
