package com.store.Demo.service;

import com.store.dto.ProductAggregatedResponseDTO;
import com.store.dto.ProductDTO;
import com.store.exception.NoProductAvailableException;
import com.store.exception.type.ExceptionType;
import com.store.model.ProductEntity;
import com.store.repository.ProductRepository;
import com.store.service.implementation.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ConversionService conversionService;

    @Test
    void findAllProductsDTO_shouldReturnConvertedList() {
        ProductEntity entity1 = ProductEntity.builder().id(1L).title("Product A").availableQuantity(10L).price(50.0).build();
        ProductEntity entity2 = ProductEntity.builder().id(2L).title("Product B").availableQuantity(5L).price(30.0).build();

        ProductDTO dto1 = new ProductDTO(1L, "Product A", 10L, 50.0);
        ProductDTO dto2 = new ProductDTO(2L, "Product B", 5L, 30.0);

        when(productRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(conversionService.convert(entity1, ProductDTO.class)).thenReturn(dto1);
        when(conversionService.convert(entity2, ProductDTO.class)).thenReturn(dto2);

        List<ProductDTO> result = productService.findAllProductsDTO();

        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    void findAllProductsDTO_shouldCallConversionService() {
        ProductEntity entity = ProductEntity.builder().id(1L).title("Product A").availableQuantity(10L).price(50.0).build();
        ProductDTO dto = new ProductDTO(1L, "Product A", 10L, 50.0);

        when(productRepository.findAll()).thenReturn(List.of(entity));
        when(conversionService.convert(entity, ProductDTO.class)).thenReturn(dto);

        List<ProductDTO> result = productService.findAllProductsDTO();

        assertThat(result).containsExactly(dto);
        verify(conversionService).convert(entity, ProductDTO.class);
    }


    @Test
    void findProductById_whenProductExists_shouldReturnEntity() {
        ProductEntity entity = ProductEntity.builder().id(1L).title("Product A").availableQuantity(10L).price(50.0).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));

        ProductEntity result = productService.findProductById(1L);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void findProductById_whenNotFound_shouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findProductById(1L))
                .isInstanceOf(NoProductAvailableException.class)
                .extracting("exceptionType")
                .isEqualTo(ExceptionType.NO_AVAILABLE_PRODUCT_FOUND);
    }

    @Test
    void adjustProductStock_whenNotEnoughStock_shouldThrowException() {
        ProductEntity entity = ProductEntity.builder().id(1L).availableQuantity(2L).price(50.0).build();

        assertThatThrownBy(() -> productService.adjustProductStock(entity, 5L))
                .isInstanceOf(NoProductAvailableException.class)
                .extracting("exceptionType")
                .isEqualTo(ExceptionType.NOT_ENOUGH_PRODUCTS_FOUND);
    }

    @Test
    void findProductAggregatedDTO_shouldReturnAggregatedResponse() {
        ProductEntity productEntity = ProductEntity
                .builder()
                .id(1L)
                .title("Product A")
                .availableQuantity(10L)
                .price(50.0)
                .build();

        ProductDTO productDTO = ProductDTO.builder()
                .id(1L)
                .title("Product A")
                .price(50.0)
                .available(10L)
                .build();

        when(productRepository.findAll()).thenReturn(List.of(productEntity));
        when(conversionService.convert(productEntity, ProductDTO.class)).thenReturn(productDTO);

        ProductAggregatedResponseDTO response = productService.findProductAggregatedDTO();

        assertThat(response).isNotNull();
        assertThat(response.products()).hasSize(1);
        assertThat(response.products().getFirst().id()).isEqualTo(1L);

        assertThat(response.priceGrouping()).isNotNull();
        assertThat(response.priceGrouping().cheap()).isEqualTo(1);
        assertThat(response.priceGrouping().medium()).isEqualTo(0);
        assertThat(response.priceGrouping().expensive()).isEqualTo(0);

        assertThat(response.availability()).isNotNull();
        assertThat(response.availability().availableProducts()).isEqualTo(1);
        assertThat(response.availability().unavailableProducts()).isEqualTo(0);

        verify(productRepository, times(3)).findAll();
        verify(conversionService).convert(productEntity, ProductDTO.class);
    }

    @Test
    void adjustEveryProductInStock_shouldNotThrowException() {
        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .price(50.0)
                .availableQuantity(5L)
                .build();

        Map<Long, Long> productQuantityMap = Map.of(1L, 3L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatCode(() -> productService.adjustEveryProductInStock(productQuantityMap))
                .doesNotThrowAnyException();

        verify(productRepository).findById(1L);
    }

    @Test
    void adjustEveryProductInStock_shouldThrowException_whenNotEnoughStock() {
        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .availableQuantity(2L)
                .build();

        Map<Long, Long> productQuantityMap = Map.of(1L, 5L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.adjustEveryProductInStock(productQuantityMap))
                .isInstanceOf(NoProductAvailableException.class)
                .hasMessageContaining(String.format("Not enough available products. Requested: %d, Available: %d", productQuantityMap.get(1L), product.getAvailableQuantity()));
    }

    @Test
    void calculateOrderPrice_shouldReturnCorrectTotal() {
        ProductEntity product1 = ProductEntity.builder().id(1L).price(10.0).build();
        ProductEntity product2 = ProductEntity.builder().id(2L).price(20.0).build();

        Map<Long, Long> productQuantityMap = Map.of(1L, 2L, 2L, 3L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        Double totalPrice = productService.calculateOrderPrice(productQuantityMap);

        assertThat(totalPrice).isEqualTo(2 * 10.0 + 3 * 20.0);
        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
    }

    @Test
    void updateProductStock_shouldUpdateQuantityAndSaveAll() {
        ProductEntity product1 = ProductEntity.builder().id(1L).availableQuantity(10L).build();
        ProductEntity product2 = ProductEntity.builder().id(2L).availableQuantity(5L).build();

        Map<Long, Long> productQuantityMap = Map.of(1L, 3L, 2L, 2L);

        when(productRepository.findAllById(anySet())).thenReturn(List.of(product1, product2));

        productService.updateProductStock(productQuantityMap);

        assertThat(product1.getAvailableQuantity()).isEqualTo(7L);
        assertThat(product2.getAvailableQuantity()).isEqualTo(3L);

        verify(productRepository).findAllById(anySet());
        verify(productRepository).saveAll(List.of(product1, product2));
    }


}
