package com.store.Demo.service;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        ProductEntity entity1 = ProductEntity.builder().id(1L).title("Product A").available(10L).price(50.0).build();
        ProductEntity entity2 = ProductEntity.builder().id(2L).title("Product B").available(5L).price(30.0).build();

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
        ProductEntity entity = ProductEntity.builder().id(1L).title("Product A").available(10L).price(50.0).build();
        ProductDTO dto = new ProductDTO(1L, "Product A", 10L, 50.0);

        when(productRepository.findAll()).thenReturn(List.of(entity));
        when(conversionService.convert(entity, ProductDTO.class)).thenReturn(dto);

        List<ProductDTO> result = productService.findAllProductsDTO();

        assertThat(result).containsExactly(dto);
        verify(conversionService).convert(entity, ProductDTO.class); // verify converter is called
    }


    @Test
    void findProductById_whenProductExists_shouldReturnEntity() {
        ProductEntity entity = ProductEntity.builder().id(1L).title("Product A").available(10L).price(50.0).build();
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
                .isEqualTo(ExceptionType.NO_AVAILABLE_PRODUCTS_FOUND);
    }

    @Test
    void adjustProductStock_whenEnoughStock_shouldReduceAndSave() {
        ProductEntity entity = ProductEntity.builder().id(1L).available(10L).price(50.0).build();

        productService.adjustProductStock(entity, 4L);

        assertThat(entity.getAvailable()).isEqualTo(6L);
        verify(productRepository).save(entity);
    }

    @Test
    void adjustProductStock_whenNotEnoughStock_shouldThrowException() {
        ProductEntity entity = ProductEntity.builder().id(1L).available(2L).price(50.0).build();

        assertThatThrownBy(() -> productService.adjustProductStock(entity, 5L))
                .isInstanceOf(NoProductAvailableException.class)
                .extracting("exceptionType")
                .isEqualTo(ExceptionType.NOT_ENOUGH_PRODUCTS_FOUND);
    }
}
