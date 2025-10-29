package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.product.Product;
import com.example.intergalactic_marketplace.dto.product.CreateProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductDto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-28T23:04:07+0200",
    comments = "version: 1.6.2, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toProduct(UUID uuid, CreateProductDto dto) {
        if ( uuid == null && dto == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        if ( dto != null ) {
            product.name( dto.getName() );
            product.description( dto.getDescription() );
            product.price( dto.getPrice() );
        }
        product.uuid( uuid );

        return product.build();
    }

    @Override
    public ProductDto toProductDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDto.ProductDtoBuilder productDto = ProductDto.builder();

        productDto.uuid( product.getUuid() );
        productDto.name( product.getName() );
        productDto.description( product.getDescription() );
        productDto.price( product.getPrice() );

        return productDto.build();
    }

    @Override
    public List<ProductDto> toProductDtoList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductDto> list = new ArrayList<ProductDto>( products.size() );
        for ( Product product : products ) {
            list.add( toProductDto( product ) );
        }

        return list;
    }
}
