package com.store.service;

import com.store.dto.ProductAggregatedResponseDTO;
import com.store.dto.ProductDTO;
import com.store.model.ProductEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {

    List<ProductDTO> findAllProductsDTO();

    ProductEntity findProductById(Long id);

    void adjustProductStock(ProductEntity productEntity, Long requestedQuantity);

    ProductAggregatedResponseDTO findProductAggregatedDTO();

    void adjustEveryProductInStock(Map<Long, Long> productIdToQuantityMap);

    Double calculateOrderPrice(Map<Long, Long> productIdToQuantityMap);

    void updateProductStock(Map<Long, Long> productIdToNewQuantityMap);
}
