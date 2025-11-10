package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.product.Product;
import com.example.intergalactic_marketplace.domain.product.ProductCategory;
import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProducts;
import com.example.intergalactic_marketplace.dto.product.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "price", source = "dto.price")
    Product toProduct(UUID uuid, SaveProductDto dto);

    @Mapping(target = "uuid", source = "product.uuid")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", source = "product.price")
    BasicProductDto toProductDto(Product product);

    @Mapping(target = "uuid", source = "product.uuid")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "recommendedProducts", source = "recommendedProducts")
    ProductDetailDto toProductDetailDto(Product product, RecommendedProducts recommendedProducts);

    @Mapping(target = "uuid", source = "productCategory.uuid")
    @Mapping(target = "name", source = "productCategory.name")
    ProductCategoryDto toProductCategoryDto(ProductCategory productCategory);

    @Mapping(target = "uuid", source = "productDto.uuid")
    @Mapping(target = "name", source = "productDto.name")
    ProductCategory toProductCategory(UUID uuid, SaveProductCategoryDto productCategoryDto);

    List<BasicProductDto> toProductDtoList(List<Product> products);
}
