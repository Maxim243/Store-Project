package com.store.Demo.controller;

import com.store.controller.ProductController;
import com.store.dto.PriceGroupingProductDTO;
import com.store.dto.ProductAggregatedResponseDTO;
import com.store.dto.ProductAvailabilityDTO;
import com.store.dto.ProductDTO;
import com.store.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        })
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void findAllProducts_shouldReturnAggregatedResponse() throws Exception {
        List<ProductDTO> mockProducts = List.of(
                new ProductDTO(1L, "Product A", 5L, 10.0),
                new ProductDTO(2L, "Product B", 7L, 20.0)
        );

        PriceGroupingProductDTO priceGrouping = new PriceGroupingProductDTO(5L, 10L, 2L);
        ProductAvailabilityDTO availability = new ProductAvailabilityDTO(12L, 3L);

        ProductAggregatedResponseDTO mockResponse = new ProductAggregatedResponseDTO(
                mockProducts,
                priceGrouping,
                availability
        );

        when(productService.findProductAggregatedDTO()).thenReturn(mockResponse);

        mockMvc.perform(get("/products/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].id").value(1L))
                .andExpect(jsonPath("$.products[0].title").value("Product A"))
                .andExpect(jsonPath("$.products[0].price").value(10.0))
                .andExpect(jsonPath("$.products[0].available").value(5L))
                .andExpect(jsonPath("$.products[1].id").value(2L))
                .andExpect(jsonPath("$.products[1].title").value("Product B"))
                .andExpect(jsonPath("$.products[1].price").value(20.0))
                .andExpect(jsonPath("$.products[1].available").value(7L))
                .andExpect(jsonPath("$.priceGrouping.cheap").value(5L))
                .andExpect(jsonPath("$.priceGrouping.medium").value(10L))
                .andExpect(jsonPath("$.priceGrouping.expensive").value(2L))
                .andExpect(jsonPath("$.availability.availableProducts").value(12L))
                .andExpect(jsonPath("$.availability.unavailableProducts").value(3L));
    }
}




