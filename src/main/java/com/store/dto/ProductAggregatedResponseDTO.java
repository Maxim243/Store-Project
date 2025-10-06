package com.store.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductAggregatedResponseDTO(
        List<ProductDTO> products,
        PriceGroupingProductDTO priceGrouping,
        ProductAvailabilityDTO availability
) {}
