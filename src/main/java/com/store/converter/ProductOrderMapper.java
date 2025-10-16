package com.store.converter;

import com.store.model.*;
import com.store.model.embeddable.ProductOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProductOrderMapper {

    public ProductOrder toProductOrder(CartItemEntity cartItemEntity) {
        ProductEntity product = cartItemEntity.getProduct();
        return ProductOrder.builder()
                .productId(product.getId())
                .quantity(cartItemEntity.getQuantity())
                .build();
    }

    public OrderEntity toOrderEntity(List<ProductOrder> productsOrderList, UserEntity userEntity, Double orderTotalPrice, PaymentMethod paymentMethod, String shippingAddress) {
        return OrderEntity.builder()
                .user(userEntity)
                .products(productsOrderList)
                .totalOrderPrice(orderTotalPrice)
                .paymentMethod(paymentMethod)
                .shippingAddress(shippingAddress)
                .orderDateTime(LocalDateTime.now())
                .build();
    }

}
