package com.store.converter;

import com.store.dto.ProductDTO;
import com.store.model.ProductEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOConverter implements Converter<ProductEntity, ProductDTO> {

    @Override
    public ProductDTO convert(ProductEntity productEntity) {
        return ProductDTO
                .builder()
                .id(productEntity.getId())
                .title(productEntity.getTitle())
                .available(productEntity.getAvailableQuantity())
                .price(productEntity.getPrice())
                .build();
    }
}