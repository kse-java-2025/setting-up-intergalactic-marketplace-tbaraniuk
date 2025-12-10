package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.TranslationService;
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
    private final TranslationService translationService;

    public ProductController (ProductService productService, TranslationService translationService) {
        this.productService = productService;
        this.translationService = translationService;
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

    @PostMapping("/productCategory")
    public ResponseEntity<ProductCategoryDto> createProductCategory(@Valid @RequestBody SaveProductCategoryDto createProductCategoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProductCategory(createProductCategoryDto));
    }

    @PutMapping("/productCategory/{productCategoryId}")
    public ResponseEntity<ProductCategoryDto> updateProductCategory(@PathVariable UUID productCategoryId, @Valid @RequestBody SaveProductCategoryDto productCategoryDto) {
        return ResponseEntity.ok(productService.updateProductCategory(productCategoryId, productCategoryDto));
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

    @PostMapping("/{productId}/translate")
    public ResponseEntity<String> translateProduct (@PathVariable UUID productId, @RequestBody String language) {
        String translatedText = translationService.translateProduct(productId.toString(), language);

        return ResponseEntity.ok(translatedText);
    }
}
