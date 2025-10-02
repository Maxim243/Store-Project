package com.store.service.implementation;

import com.store.dto.PriceGroupingProductDTO;
import com.store.dto.ProductAggregatedResponseDTO;
import com.store.dto.ProductAvailabilityDTO;
import com.store.dto.ProductDTO;
import com.store.exception.NoProductAvailableException;
import com.store.exception.type.ExceptionType;
import com.store.model.ProductEntity;
import com.store.repository.ProductRepository;
import com.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ConversionService conversionService;

    @Override
    public ProductAggregatedResponseDTO findProductAggregatedDTO() {
        CompletableFuture<List<ProductDTO>> allProductsFuture = asyncFindAllProducts();
        CompletableFuture<PriceGroupingProductDTO> priceRangeProductsFuture = asyncGroupByPriceRange();
        CompletableFuture<ProductAvailabilityDTO> availabilityFuture = asyncGroupByAvailability();

        CompletableFuture.allOf(allProductsFuture, priceRangeProductsFuture, availabilityFuture).join();

        return ProductAggregatedResponseDTO.builder()
                .products(allProductsFuture.join())
                .priceGrouping(priceRangeProductsFuture.join())
                .availability(availabilityFuture.join())
                .build();
    }

    @Override
    public List<ProductDTO> findAllProductsDTO() {
        return productRepository.findAll()
                .stream()
                .map(productEntity -> conversionService.convert(productEntity, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductEntity findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> NoProductAvailableException.of(ExceptionType.NO_AVAILABLE_PRODUCT_FOUND));
    }

    @Override
    public void adjustProductStock(ProductEntity productEntity, Long requestedQuantity) {
        if (productEntity.getAvailableQuantity() < requestedQuantity) {
            throw new NoProductAvailableException(ExceptionType.NOT_ENOUGH_PRODUCTS_FOUND, requestedQuantity, productEntity.getAvailableQuantity());
        }
    }

    @Override
    public void adjustEveryProductInStock(Map<Long, Long> productIdToQuantityMap) {
        productIdToQuantityMap.forEach((productId, quantityRequested) -> {
            ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> NoProductAvailableException.of(ExceptionType.NO_AVAILABLE_PRODUCT_FOUND));
            adjustProductStock(productEntity, quantityRequested);
        });
    }

    @Override
    public Double calculateOrderPrice(Map<Long, Long> productIdToQuantityMap) {
        return productIdToQuantityMap.entrySet().stream()
                .mapToDouble(entry -> {
                    ProductEntity product = findProductById(entry.getKey());
                    return product.getPrice() * entry.getValue();
                })
                .sum();
    }

    @Override
    @Transactional
    public void updateProductStock(Map<Long, Long> productIdToQuantityRequestedMap) {
        List<ProductEntity> products = productRepository.findAllById(productIdToQuantityRequestedMap.keySet());

        for (ProductEntity product : products) {
            Long orderedQuantity = productIdToQuantityRequestedMap.get(product.getId());
            product.setAvailableQuantity(product.getAvailableQuantity() - orderedQuantity);
        }

        productRepository.saveAll(products);
    }


    private PriceGroupingProductDTO groupByPriceRange() {
        List<ProductEntity> products = productRepository.findAll();

        Long cheap = products.stream().filter(p -> p.getPrice() < 100).count();
        Long medium = products.stream().filter(p -> p.getPrice() >= 100 && p.getPrice() < 500).count();
        Long expensive = products.stream().filter(p -> p.getPrice() >= 500).count();

        return PriceGroupingProductDTO
                .builder()
                .cheap(cheap)
                .medium(medium)
                .expensive(expensive)
                .build();
    }

    private ProductAvailabilityDTO groupByAvailability() {
        List<ProductEntity> products = productRepository.findAll();

        Long available = products.stream().filter(productEntity -> productEntity.getAvailableQuantity() > 0).count();
        Long unavailable = products.stream().filter(productEntity -> productEntity.getAvailableQuantity() < 1).count();

        return ProductAvailabilityDTO
                .builder()
                .availableProducts(available)
                .unavailableProducts(unavailable)
                .build();
    }

    private CompletableFuture<List<ProductDTO>> asyncFindAllProducts() {
        return CompletableFuture.supplyAsync(this::findAllProductsDTO);
    }

    private CompletableFuture<PriceGroupingProductDTO> asyncGroupByPriceRange() {
        return CompletableFuture.supplyAsync(this::groupByPriceRange);
    }

    private CompletableFuture<ProductAvailabilityDTO> asyncGroupByAvailability() {
        return CompletableFuture.supplyAsync(this::groupByAvailability);
    }
}
