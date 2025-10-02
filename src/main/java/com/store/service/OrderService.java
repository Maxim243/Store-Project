package com.store.service;

import com.store.dto.CheckOutOrderDTO;
import com.store.dto.OrderResponseDTO;
import com.store.model.CartEntity;
import com.store.model.OrderEntity;
import com.store.model.UserEntity;

import java.util.Map;

public interface OrderService {

    OrderResponseDTO addOrder(CheckOutOrderDTO checkOutOrderDTO, String userEmail);

    OrderEntity saveOrder(CheckOutOrderDTO checkOutOrderDTO, CartEntity cart, UserEntity user, Map<Long, Long> productToQuantityMap);
}
