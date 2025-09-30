package com.store.dto;

import lombok.Builder;

@Builder
public record AddItemDTO(Long id,
                         Long quantity) {
}
