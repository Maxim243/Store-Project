package com.store.repository;

import com.store.model.CartEntity;
import com.store.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByCartId(Long cartId);

    Optional<CartItemEntity> findByCartIdAndProductId(Long cartId, Long productId);
}
