package com.example.maritime.controller;

import com.example.maritime.model.Product;
import com.example.maritime.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @PostMapping("/products")
    public Product create(@Valid @RequestBody Product product) {
        return productRepository.save(product);
    }
}
