package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.product.Product;
import com.example.intergalactic_marketplace.dto.product.CreateProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductDto;
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
    Product toProduct(UUID uuid, CreateProductDto dto);

    @Mapping(target = "uuid", source = "product.uuid")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", source = "product.price")
    ProductDto toProductDto(Product product);

    List<ProductDto> toProductDtoList(List<Product> products);
}
