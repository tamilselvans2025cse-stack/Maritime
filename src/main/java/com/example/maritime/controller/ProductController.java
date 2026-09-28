package com.example.maritime.controller;

import com.example.maritime.model.Product;
import com.example.maritime.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products";
    }

    @PostMapping("/products")
    public String createProduct(@RequestParam String productCode, @RequestParam String productName,
                                 @RequestParam String productType, @RequestParam java.math.BigDecimal unitPrice,
                                 @RequestParam(required = false) String description) {
        Product product = Product.builder()
                .productCode(productCode).productName(productName)
                .productType(com.example.maritime.model.ProductType.valueOf(productType))
                .unitPrice(unitPrice).description(description)
                .build();
        productRepository.save(product);
        return "redirect:/products";
    }
}
