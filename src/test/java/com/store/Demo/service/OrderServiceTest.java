package com.store.Demo.service;

import com.store.converter.ProductOrderMapper;
import com.store.dto.CheckOutOrderDTO;
import com.store.dto.OrderResponseDTO;
import com.store.dto.ProductOrderDTO;
import com.store.model.CartEntity;
import com.store.model.CartItemEntity;
import com.store.model.OrderEntity;
import com.store.model.UserEntity;
import com.store.model.embeddable.ProductOrder;
import com.store.repository.OrderRepository;
import com.store.service.CartItemService;
import com.store.service.CartService;
import com.store.service.ProductService;
import com.store.service.UserService;
import com.store.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private CartItemService cartItemService;

    @Mock
    private ProductOrderMapper productOrderMapper;

    @Mock
    private ConversionService conversionService;


    @Test
    void addOrder_shouldReturnOrderResponse() {
        Double totalPrice = 100.0;

        String userEmail = "test@example.com";

        CartEntity cart = CartEntity.builder()
                .id(1L)
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .email(userEmail)
                .build();

        ProductOrderDTO productOrderDTO = ProductOrderDTO.builder()
                .productId(1L)
                .quantity(2L)
                .build();

        CheckOutOrderDTO checkOutOrderDTO = CheckOutOrderDTO.builder()
                .orderItems(List.of(productOrderDTO))
                .build();

        OrderEntity orderEntity = OrderEntity.builder()
                .id(1L)
                .build();

        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
                .orderId(orderEntity.getId())
                .build();

        when(cartService.findCartByUserEmail(userEmail)).thenReturn(cart);
        when(userService.findByEmail(userEmail)).thenReturn(user);
        when(cartItemService.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(CartItemEntity.builder().id(1L).build());
        when(productOrderMapper.toProductOrder(any())).thenReturn(ProductOrder.builder().build());
        when(productService.calculateOrderPrice(anyMap())).thenReturn(totalPrice);
        when(productOrderMapper.toOrderEntity(anyList(), eq(user), eq(totalPrice), any(), any()))
                .thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(conversionService.convert(orderEntity, OrderResponseDTO.class)).thenReturn(orderResponseDTO);

        OrderResponseDTO response = orderService.addOrder(checkOutOrderDTO, userEmail);

        verify(productService).adjustEveryProductInStock(anyMap());
        verify(cartItemService).removeAllItemsFromCartByIds(anySet(), eq(userEmail));
        verify(cartService).calculateCartTotalPrice(cart);
        verify(productService).updateProductStock(anyMap());
        verify(orderRepository).save(orderEntity);
        verify(conversionService).convert(orderEntity, OrderResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo(orderEntity.getId());
    }


    @Test
    void saveOrder_shouldReturnSavedOrder() {
        Double totalOrderPrice = 100.0;

        CartEntity cart = CartEntity.builder()
                .id(1L)
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        CheckOutOrderDTO checkOutOrderDTO = mock(CheckOutOrderDTO.class);

        ProductOrderDTO orderItem = ProductOrderDTO.builder()
                .productId(1L)
                .quantity(2L)
                .build();
        when(checkOutOrderDTO.orderItems()).thenReturn(List.of(orderItem));

        CartItemEntity cartItem = CartItemEntity.builder()
                .id(1L)
                .build();
        ProductOrder productOrder = ProductOrder.builder()
                .productId(orderItem.productId())
                .quantity(orderItem.quantity())
                .build();

        when(cartItemService.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(cartItem);
        when(productOrderMapper.toProductOrder(cartItem)).thenReturn(productOrder);
        when(productService.calculateOrderPrice(anyMap())).thenReturn(totalOrderPrice);

        OrderEntity expectedOrder = OrderEntity.builder()
                .id(1L)
                .totalOrderPrice(totalOrderPrice)
                .build();
        when(productOrderMapper.toOrderEntity(anyList(), eq(user), eq(totalOrderPrice), any(), any())).thenReturn(expectedOrder);
        when(orderRepository.save(expectedOrder)).thenReturn(expectedOrder);

        Map<Long, Long> productToQuantityMap = Map.of(orderItem.productId(), orderItem.quantity());

        OrderEntity savedOrder = orderService.saveOrder(checkOutOrderDTO, cart, user, productToQuantityMap);

        assertThat(savedOrder).isEqualTo(expectedOrder);

        verify(cartItemService).findByCartIdAndProductId(cart.getId(), orderItem.productId());
        verify(productOrderMapper).toProductOrder(cartItem);
        verify(productService).calculateOrderPrice(productToQuantityMap);
        verify(orderRepository).save(expectedOrder);
    }

}