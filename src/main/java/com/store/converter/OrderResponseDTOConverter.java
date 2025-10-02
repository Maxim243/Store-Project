package com.store.converter;

import com.store.dto.OrderResponseDTO;
import com.store.dto.ProductOrderDTO;
import com.store.model.OrderEntity;
import com.store.model.embeddable.ProductOrder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseDTOConverter implements Converter<OrderEntity, OrderResponseDTO> {
    @Override
    public OrderResponseDTO convert(OrderEntity orderEntity) {
        return OrderResponseDTO
                .builder()
                .orderId(orderEntity.getId())
                .products(orderEntity.getProducts()
                        .stream()
                        .map(this::convertToProductOrderDTO)
                        .toList())
                .orderCreatedDateTime(orderEntity.getOrderDateTime())
                .totalPrice(orderEntity.getTotalOrderPrice())
                .build();
    }

    private ProductOrderDTO convertToProductOrderDTO(ProductOrder productOrder) {
        return ProductOrderDTO.builder()
                .productId(productOrder.getProductId())
                .quantity(productOrder.getQuantity())
                .build();
    }
}
