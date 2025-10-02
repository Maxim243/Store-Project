package com.store.dto;

import com.store.model.PaymentMethod;
import lombok.Builder;

import java.util.List;

@Builder
public record CheckOutOrderDTO(List<ProductOrderDTO> orderItems,
                               String shippingAddress,
                               PaymentMethod paymentMethod) {
}
