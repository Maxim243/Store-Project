package com.store.dto;

import lombok.Builder;

@Builder
public record PriceGroupingProductDTO(Long cheap,
                                      Long medium,
                                      Long expensive) {
}
