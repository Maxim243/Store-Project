package com.store.service.implementation;

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
import com.store.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final UserService userService;
    private final CartItemService cartItemService;

    private final ProductOrderMapper productOrderMapper;
    private final ConversionService conversionService;

    @Override
    @Transactional
    public OrderResponseDTO addOrder(CheckOutOrderDTO checkOutOrderDTO, String userEmail) {
        CartEntity cart = cartService.findCartByUserEmail(userEmail);
        UserEntity user = userService.findByEmail(userEmail);
        Map<Long, Long> productToQuantityMap = mapProductIdToQuantity(checkOutOrderDTO);

        productService.adjustEveryProductInStock(productToQuantityMap);
        OrderEntity orderCreated = saveOrder(checkOutOrderDTO, cart, user, productToQuantityMap);

        cartItemService.removeAllItemsFromCartByIds(productToQuantityMap.keySet(), userEmail);
        cartService.calculateCartTotalPrice(cart);
        productService.updateProductStock(productToQuantityMap);

        return conversionService.convert(orderCreated, OrderResponseDTO.class);
    }

    @Override
    public OrderEntity saveOrder(CheckOutOrderDTO checkOutOrderDTO, CartEntity cart, UserEntity user, Map<Long, Long> productToQuantityMap) {
        OrderEntity orderCreated = createOrder(getCartItemEntitiesToBeOrdered(checkOutOrderDTO, cart), user, productToQuantityMap, checkOutOrderDTO);
        return orderRepository.save(orderCreated);
    }

    private List<CartItemEntity> getCartItemEntitiesToBeOrdered(CheckOutOrderDTO checkOutOrderDTO, CartEntity cart) {
        return checkOutOrderDTO.orderItems()
                .stream()
                .map(productOrderDTO -> cartItemService.findByCartIdAndProductId(cart.getId(), productOrderDTO.productId()))
                .toList();
    }

    private Map<Long, Long> mapProductIdToQuantity(CheckOutOrderDTO checkOutOrderDTO) {
        return checkOutOrderDTO
                .orderItems()
                .stream()
                .collect(Collectors.toMap(ProductOrderDTO::productId, ProductOrderDTO::quantity));
    }


    private List<ProductOrder> mapCartItemsToProductOrders(List<CartItemEntity> cartItemEntitiesToBeOrdered) {
        return cartItemEntitiesToBeOrdered
                .stream()
                .map(productOrderMapper::toProductOrder)
                .toList();
    }

    private OrderEntity createOrder(List<CartItemEntity> cartItemEntitiesToBeOrdered, UserEntity user, Map<Long, Long> productToQuantityMap, CheckOutOrderDTO checkOutOrderDTO) {
        Double orderPrice = productService.calculateOrderPrice(productToQuantityMap);
        List<ProductOrder> productOrders = mapCartItemsToProductOrders(cartItemEntitiesToBeOrdered);
        return productOrderMapper.toOrderEntity(
                productOrders, user, orderPrice, checkOutOrderDTO.paymentMethod(), checkOutOrderDTO.shippingAddress()
        );
    }

}