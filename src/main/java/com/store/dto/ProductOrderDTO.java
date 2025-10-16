package com.store.dto;

import lombok.Builder;

@Builder
public record ProductOrderDTO(Long productId,
                              Long quantity) {
}
