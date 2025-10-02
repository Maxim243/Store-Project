package com.store.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponseDTO(
        Long orderId,
        Double totalPrice,
        List<ProductOrderDTO> products,
        LocalDateTime orderCreatedDateTime
) {

}

