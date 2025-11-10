package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.product.Product;
import com.example.intergalactic_marketplace.domain.product.ProductCategory;
import com.example.intergalactic_marketplace.dto.product.*;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "price", source = "dto.price")
    @Mapping(target = "categories", source = "categories")
    Product toProduct(UUID uuid, SaveProductDto dto, Set<UUID> categories);

    @Mapping(target = "uuid", source = "product.uuid")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "categories", source = "categories")
    ProductBasicDto toProductDto(Product product, Set<ProductCategoryDto> categories);

    @Mapping(target = "uuid", source = "product.uuid")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "recommendedProducts", source = "recommendedProducts.recommendedProducts")
    ProductDetailDto toProductDetailDto(Product product, Set<ProductCategoryDto> categories, RecommendedProductsDto recommendedProducts);

    @Mapping(target = "uuid", source = "productCategory.uuid")
    @Mapping(target = "name", source = "productCategory.name")
    ProductCategoryDto toProductCategoryDto(ProductCategory productCategory);

    @Mapping(target = "uuid", source = "productId")
    @Mapping(target = "name", source = "productCategoryDto.name")
    ProductCategory toProductCategory(UUID productId, SaveProductCategoryDto productCategoryDto);
}
