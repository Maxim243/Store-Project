package com.store.dto;

import lombok.Builder;

@Builder
public record ProductAvailabilityDTO(
        Long availableProducts,
        Long unavailableProducts
) {
}
