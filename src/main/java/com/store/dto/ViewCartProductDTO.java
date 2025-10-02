package com.store.dto;

import lombok.Builder;

@Builder
public record ViewCartProductDTO(Long id,
                                 String title,
                                 Double price,
                                 Long quantity,
                                 Long ordinal) {
}
