package com.store.dto;

import lombok.Builder;

@Builder
public record ProductDTO(Long id,
                         String title,
                         Long available,
                         Double price) {
}
