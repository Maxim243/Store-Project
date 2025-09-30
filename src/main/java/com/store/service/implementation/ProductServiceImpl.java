package com.store.service.implementation;

import com.store.dto.ProductDTO;
import com.store.exception.NoProductAvailableException;
import com.store.exception.type.ExceptionType;
import com.store.model.ProductEntity;
import com.store.repository.ProductRepository;
import com.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ConversionService conversionService;

    @Override
    public List<ProductDTO> findAllProductsDTO() {
        return productRepository.findAll()
                .stream()
                .map(productEntity -> conversionService.convert(productEntity, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductEntity findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> NoProductAvailableException.of(ExceptionType.NO_AVAILABLE_PRODUCTS_FOUND));
    }

    @Override
    public void adjustProductStock(ProductEntity productEntity, Long requestedQuantity) {
        if (productEntity.getAvailable() < requestedQuantity) {
            throw new NoProductAvailableException(ExceptionType.NOT_ENOUGH_PRODUCTS_FOUND, requestedQuantity, productEntity.getAvailable());
        }
        productEntity.setAvailable(productEntity.getAvailable() - requestedQuantity);
        productRepository.save(productEntity);
    }
}
