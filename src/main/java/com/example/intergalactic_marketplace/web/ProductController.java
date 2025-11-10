package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<ProductBasicDto>> getAllProducts (Pageable pageable) {
        Page<ProductBasicDto> productPate = productService.getAllProducts(pageable);

        return ResponseEntity.ok(productPate);
    }

    @PostMapping
    public ResponseEntity<ProductBasicDto> createProduct (@Valid @RequestBody SaveProductDto createProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @PostMapping("/createCategory")
    public ResponseEntity<ProductCategoryDto> createProductCategory(@Valid @RequestBody SaveProductCategoryDto createProductCategoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProductCategory(createProductCategoryDto));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductBasicDto> getProduct (
        @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductBasicDto> updateProduct (
        @PathVariable UUID productId,
        @Valid @RequestBody SaveProductDto productDto
    ) {
        return ResponseEntity.ok(productService.updateProduct(productId, productDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct (@PathVariable UUID productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }
}
