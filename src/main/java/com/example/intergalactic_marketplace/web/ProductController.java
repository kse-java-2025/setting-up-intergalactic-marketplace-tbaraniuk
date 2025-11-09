package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.dto.product.CreateProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductDto;
import com.example.intergalactic_marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@Validated
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController (ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> getAllProducts () {
        return productService.getAllProducts();
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct (@Valid @RequestBody CreateProductDto createProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct (
        @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct (
        @PathVariable UUID productId,
        @Valid @RequestBody CreateProductDto productDto
    ) {
        return ResponseEntity.ok(productService.updateProduct(productId, productDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct (@PathVariable UUID productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }
}
