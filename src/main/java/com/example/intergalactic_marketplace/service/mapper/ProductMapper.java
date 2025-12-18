package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.ProductDetailDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import com.example.intergalactic_marketplace.entity.ProductCategoryEntity;
import com.example.intergalactic_marketplace.entity.ProductEntity;
import com.example.intergalactic_marketplace.repository.projection.ProductBasicProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", source = "dto.sku")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "price", source = "dto.price")
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toProductEntity(SaveProductDto dto);

    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    ProductBasicDto toProductBasicDto(ProductBasicProjection product);

    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "categories", source = "product.categories")
    @Mapping(target = "createdAt", source = "product.createdAt")
    @Mapping(target = "updatedAt", source = "product.updatedAt")
    @Mapping(target = "recommendedProducts", source = "recommendedProducts.recommendedProducts")
    ProductDetailDto toProductDetailDto(ProductEntity product, RecommendedProductsDto recommendedProducts);

    default ProductDetailDto toProductDetailDto(ProductEntity product) {
        return toProductDetailDto(product, null);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProductEntityFromDto(SaveProductDto dto, @MappingTarget ProductEntity productEntity);

    @Mapping(target = "id", source = "productCategory.id")
    @Mapping(target = "name", source = "productCategory.name")
    ProductCategoryDto toProductCategoryDto(ProductCategoryEntity productCategory);

    @Mapping(target = "name", source = "productCategoryDto.name")
    ProductCategoryEntity toProductCategoryEntity(SaveProductCategoryDto productCategoryDto);

    @Mapping(target = "id", ignore = true)
    void updateProductEntityFromDto(SaveProductCategoryDto productCategoryDto, @MappingTarget ProductCategoryEntity productCategoryEntity);
}
