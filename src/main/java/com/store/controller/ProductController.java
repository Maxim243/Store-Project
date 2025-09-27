package com.store.controller;

import com.store.dto.ProductAggregatedResponseDTO;
import com.store.dto.ProductDTO;
import com.store.service.ProductService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@Data
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public ProductAggregatedResponseDTO findAllProducts() {
        return productService.findProductAggregatedDTO();
    }
}
